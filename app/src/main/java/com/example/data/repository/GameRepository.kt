package com.example.data.repository

import com.example.data.cloud.CloudAccountOption
import com.example.data.cloud.CloudBattleRecord
import com.example.data.cloud.CloudConnectionStatus
import com.example.data.cloud.CloudUserProfile
import com.example.data.cloud.FirebaseCloudDatabase
import com.example.data.cloud.OnlineLeaderboardPlayer
import com.example.data.db.AppDatabase
import com.example.data.db.BattleRecordEntity
import com.example.data.db.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

/**
 * GameRepository
 * مستودع اللعبة المركزي - يدعم قاعدة بيانات جوجل فايربيز السحابية (Google Firebase Cloud Firestore)
 * مع المزامنة التلقائية والاحتفاظ بنسخة محلية (Cache) لضمان سرعة الاستجابة وحفظ تقدم اللاعبين.
 */
class GameRepository(
    private val database: AppDatabase,
    val firebaseDb: FirebaseCloudDatabase = FirebaseCloudDatabase()
) {

    private val userDao = database.userDao()
    private val battleDao = database.battleDao()

    val currentUser: Flow<UserEntity?> = userDao.getCurrentSessionUser()
    val cloudStatus: StateFlow<CloudConnectionStatus> = firebaseDb.connectionStatus
    val onlineLeaderboard: StateFlow<List<OnlineLeaderboardPlayer>> = firebaseDb.onlineLeaderboard

    suspend fun checkCloudPing(): Int = withContext(Dispatchers.IO) {
        firebaseDb.checkCloudLatency()
    }

    suspend fun getAvailableAccounts(): List<CloudAccountOption> = withContext(Dispatchers.IO) {
        firebaseDb.fetchAvailableCloudAccounts()
    }

    suspend fun fetchCloudSoundAsset(context: android.content.Context, soundKey: String): ShortArray? = withContext(Dispatchers.IO) {
        firebaseDb.fetchCloudSoundAsset(context, soundKey)
    }

    suspend fun getCurrentUserSync(): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getCurrentSessionUserSync()
    }

    suspend fun registerWithEmail(username: String, email: String, password: String): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            val trimmedEmail = email.trim().lowercase()

            // 1. إنشاء الحساب أونلاين في قاعدة بيانات فايربيز السحابية أولاً
            val cloudResult = firebaseDb.registerCloudUser(username, trimmedEmail, password)
            if (cloudResult.isFailure) {
                return@withContext Result.failure(cloudResult.exceptionOrNull() ?: Exception("فشل الاتصال بقاعدة بيانات فايربيز"))
            }
            val cloudUser = cloudResult.getOrThrow()

            // 2. مزامنة وحفظ البيانات سحابياً في فايربيز ومحلياً
            val newUser = UserEntity(
                username = cloudUser.username,
                email = cloudUser.email,
                passwordHash = cloudUser.passwordHash,
                authProvider = cloudUser.authProvider,
                level = cloudUser.level,
                xp = cloudUser.xp,
                gold = cloudUser.gold,
                wins = cloudUser.wins,
                losses = cloudUser.losses,
                equippedWeaponId = cloudUser.equippedWeaponId,
                equippedSuperpowerId = cloudUser.equippedSuperpowerId,
                unlockedWeaponIds = cloudUser.unlockedWeaponIds.joinToString(","),
                unlockedSuperpowerIds = cloudUser.unlockedSuperpowerIds.joinToString(","),
                isCurrentSession = true
            )

            userDao.clearActiveSessions()
            val newId = userDao.insertUser(newUser)
            val created = newUser.copy(id = newId)
            Result.success(created)
        }

    suspend fun loginWithEmail(email: String, password: String): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            val trimmedEmail = email.trim().lowercase()

            // 1. تسجيل الدخول والتحقق عبر قاعدة بيانات جوجل فايربيز
            val cloudResult = firebaseDb.loginCloudUser(trimmedEmail, password)
            if (cloudResult.isFailure) {
                return@withContext Result.failure(cloudResult.exceptionOrNull() ?: Exception("فشل التحقق من الحساب في فايربيز"))
            }
            val cloudUser = cloudResult.getOrThrow()

            // 2. تحديث وتفعيل الجلسة الحالية
            val existing = userDao.getUserByEmail(trimmedEmail)
            userDao.clearActiveSessions()

            val targetUser = if (existing != null) {
                val updated = existing.copy(
                    username = cloudUser.username,
                    level = cloudUser.level.coerceAtLeast(existing.level),
                    gold = cloudUser.gold.coerceAtLeast(existing.gold),
                    xp = cloudUser.xp.coerceAtLeast(existing.xp),
                    wins = cloudUser.wins.coerceAtLeast(existing.wins),
                    losses = cloudUser.losses.coerceAtLeast(existing.losses),
                    isCurrentSession = true
                )
                userDao.updateUser(updated)
                updated
            } else {
                val newUser = UserEntity(
                    username = cloudUser.username,
                    email = cloudUser.email,
                    passwordHash = cloudUser.passwordHash,
                    authProvider = cloudUser.authProvider,
                    level = cloudUser.level,
                    xp = cloudUser.xp,
                    gold = cloudUser.gold,
                    wins = cloudUser.wins,
                    losses = cloudUser.losses,
                    equippedWeaponId = cloudUser.equippedWeaponId,
                    equippedSuperpowerId = cloudUser.equippedSuperpowerId,
                    unlockedWeaponIds = cloudUser.unlockedWeaponIds.joinToString(","),
                    unlockedSuperpowerIds = cloudUser.unlockedSuperpowerIds.joinToString(","),
                    isCurrentSession = true
                )
                val newId = userDao.insertUser(newUser)
                newUser.copy(id = newId)
            }

            Result.success(targetUser)
        }

    suspend fun loginWithGoogle(googleEmail: String, displayName: String): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            val trimmedEmail = googleEmail.trim().lowercase()
            val cloudResult = firebaseDb.loginWithCloudGoogle(trimmedEmail, displayName)
            val cloudUser = cloudResult.getOrThrow()

            val existing = userDao.getUserByEmail(trimmedEmail)
            userDao.clearActiveSessions()

            val target = if (existing != null) {
                userDao.setActiveSession(existing.id)
                existing.copy(isCurrentSession = true)
            } else {
                val newUser = UserEntity(
                    username = cloudUser.username,
                    email = cloudUser.email,
                    passwordHash = cloudUser.passwordHash,
                    authProvider = "FIREBASE_GOOGLE",
                    level = cloudUser.level,
                    xp = cloudUser.xp,
                    gold = cloudUser.gold,
                    wins = cloudUser.wins,
                    losses = cloudUser.losses,
                    equippedWeaponId = cloudUser.equippedWeaponId,
                    equippedSuperpowerId = cloudUser.equippedSuperpowerId,
                    unlockedWeaponIds = cloudUser.unlockedWeaponIds.joinToString(","),
                    unlockedSuperpowerIds = cloudUser.unlockedSuperpowerIds.joinToString(","),
                    isCurrentSession = true
                )
                val newId = userDao.insertUser(newUser)
                newUser.copy(id = newId)
            }
            Result.success(target)
        }

    suspend fun loginAsGuest(): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cloudGuestResult = firebaseDb.loginCloudGuest()
        val cloudGuest = cloudGuestResult.getOrThrow()

        val existing = userDao.getUserByEmail(cloudGuest.email)
        userDao.clearActiveSessions()

        val guestUser = if (existing != null) {
            userDao.setActiveSession(existing.id)
            existing.copy(isCurrentSession = true)
        } else {
            val entity = UserEntity(
                username = cloudGuest.username,
                email = cloudGuest.email,
                passwordHash = cloudGuest.passwordHash,
                authProvider = "FIREBASE_ANONYMOUS",
                level = cloudGuest.level,
                xp = cloudGuest.xp,
                gold = cloudGuest.gold,
                wins = cloudGuest.wins,
                losses = cloudGuest.losses,
                equippedWeaponId = cloudGuest.equippedWeaponId,
                equippedSuperpowerId = cloudGuest.equippedSuperpowerId,
                unlockedWeaponIds = cloudGuest.unlockedWeaponIds.joinToString(","),
                unlockedSuperpowerIds = cloudGuest.unlockedSuperpowerIds.joinToString(","),
                isCurrentSession = true
            )
            val newId = userDao.insertUser(entity)
            entity.copy(id = newId)
        }
        Result.success(guestUser)
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userDao.clearActiveSessions()
    }

    suspend fun equipWeapon(weaponId: Int) = withContext(Dispatchers.IO) {
        val user = userDao.getCurrentSessionUserSync() ?: return@withContext
        val updated = user.copy(equippedWeaponId = weaponId)
        userDao.updateUser(updated)
        syncUserToCloud(updated)
    }

    suspend fun equipSuperpower(superpowerId: Int) = withContext(Dispatchers.IO) {
        val user = userDao.getCurrentSessionUserSync() ?: return@withContext
        val updated = user.copy(equippedSuperpowerId = superpowerId)
        userDao.updateUser(updated)
        syncUserToCloud(updated)
    }

    suspend fun unlockWeapon(weaponId: Int, cost: Int): Boolean = withContext(Dispatchers.IO) {
        val user = userDao.getCurrentSessionUserSync() ?: return@withContext false
        if (user.gold < cost) return@withContext false

        val currentUnlocked = user.unlockedWeaponIds.split(",").filter { it.isNotBlank() }.toMutableSet()
        currentUnlocked.add(weaponId.toString())

        val updated = user.copy(
            gold = user.gold - cost,
            unlockedWeaponIds = currentUnlocked.joinToString(",")
        )
        userDao.updateUser(updated)
        syncUserToCloud(updated)
        true
    }

    suspend fun unlockSuperpower(superpowerId: Int, cost: Int): Boolean = withContext(Dispatchers.IO) {
        val user = userDao.getCurrentSessionUserSync() ?: return@withContext false
        if (user.gold < cost) return@withContext false

        val currentUnlocked = user.unlockedSuperpowerIds.split(",").filter { it.isNotBlank() }.toMutableSet()
        currentUnlocked.add(superpowerId.toString())

        val updated = user.copy(
            gold = user.gold - cost,
            unlockedSuperpowerIds = currentUnlocked.joinToString(",")
        )
        userDao.updateUser(updated)
        syncUserToCloud(updated)
        true
    }

    suspend fun recordBattleResult(
        isVictory: Boolean,
        opponentName: String,
        weaponName: String,
        superpowerName: String,
        damageDealt: Int,
        damageTaken: Int
    ) = withContext(Dispatchers.IO) {
        val user = userDao.getCurrentSessionUserSync() ?: return@withContext

        val goldReward = if (isVictory) 140 else 50
        val xpReward = if (isVictory) 160 else 60
        val newXp = user.xp + xpReward
        val newLevel = 1 + (newXp / 300)

        val updatedUser = user.copy(
            gold = user.gold + goldReward,
            xp = newXp,
            level = newLevel,
            wins = if (isVictory) user.wins + 1 else user.wins,
            losses = if (!isVictory) user.losses + 1 else user.losses
        )
        userDao.updateUser(updatedUser)

        // 1. تسجيل محلي في Room
        battleDao.insertRecord(
            BattleRecordEntity(
                userId = user.id,
                opponentName = opponentName,
                result = if (isVictory) "VICTORY" else "DEFEAT",
                weaponUsed = weaponName,
                superpowerUsed = superpowerName,
                damageDealt = damageDealt,
                damageTaken = damageTaken,
                goldEarned = goldReward
            )
        )

        // 2. مزامنة فورية مع قاعدة بيانات جوجل فايربيز (Firebase Cloud Firestore)
        syncUserToCloud(updatedUser)
        firebaseDb.recordCloudBattle(
            CloudBattleRecord(
                cloudUserId = user.email,
                opponentName = opponentName,
                result = if (isVictory) "VICTORY" else "DEFEAT",
                weaponUsed = weaponName,
                superpowerUsed = superpowerName,
                damageDealt = damageDealt,
                damageTaken = damageTaken,
                goldEarned = goldReward
            )
        )
    }

    private suspend fun syncUserToCloud(user: UserEntity) {
        val cloudProfile = CloudUserProfile(
            cloudUserId = user.email,
            username = user.username,
            email = user.email,
            passwordHash = user.passwordHash,
            authProvider = user.authProvider,
            level = user.level,
            xp = user.xp,
            gold = user.gold,
            wins = user.wins,
            losses = user.losses,
            equippedWeaponId = user.equippedWeaponId,
            equippedSuperpowerId = user.equippedSuperpowerId,
            unlockedWeaponIds = user.unlockedWeaponIds.split(",").mapNotNull { it.toIntOrNull() },
            unlockedSuperpowerIds = user.unlockedSuperpowerIds.split(",").mapNotNull { it.toIntOrNull() }
        )
        firebaseDb.syncUserProfileToCloud(cloudProfile)
    }

    fun getBattleHistory(userId: Long): Flow<List<BattleRecordEntity>> {
        return battleDao.getBattleHistoryForUser(userId)
    }
}

package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.StudioNativeSoundEngine
import com.example.audio.NarratorEngine
import com.example.data.cloud.CloudAccountOption
import com.example.data.cloud.CloudConnectionStatus
import com.example.data.cloud.OnlineLeaderboardPlayer
import com.example.data.db.AppDatabase
import com.example.data.db.BattleRecordEntity
import com.example.data.db.UserEntity
import com.example.data.model.Superpower
import com.example.data.model.SuperpowersCatalog
import com.example.data.model.Weapon
import com.example.data.model.WeaponsCatalog
import com.example.data.repository.GameRepository
import com.example.update.AppUpdateManager
import com.example.update.DownloadState
import com.example.update.UpdateInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class Screen {
    AUTH,
    HOME,
    WEAPONS,
    SUPERPOWERS,
    BATTLE,
    HISTORY,
    LEADERBOARD
}

enum class BattleTurn {
    PLAYER_TURN,
    ENEMY_TURN
}

data class BattleState(
    val inBattle: Boolean = false,
    val currentTurn: BattleTurn = BattleTurn.PLAYER_TURN,
    val turnNumber: Int = 1,
    val isActionInProgress: Boolean = false,

    // بيانات الوحش / الخصم
    val opponentName: String = "",
    val opponentMaxHp: Int = 300,
    val opponentHp: Int = 300,
    val opponentEnergy: Int = 35,
    val opponentWeaponId: Int = 1,
    val opponentSuperpowerId: Int = 1,
    val opponentLastAction: String = "",

    // بيانات اللاعب
    val playerMaxHp: Int = 300,
    val playerHp: Int = 300,
    val playerEnergy: Int = 40,
    val isPlayerDefending: Boolean = false,

    // سجل المعركة وحالة الانتهاء
    val battleLog: List<String> = emptyList(),
    val isBattleOver: Boolean = false,
    val isVictory: Boolean = false,
    val goldEarned: Int = 0,
    val xpEarned: Int = 0
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = GameRepository(database)

    // مكتبة المؤثرات الصوتية الاحترافية الأصلية للألعاب 2026 (Studio Game Audio Engine Native)
    val audioEngine = StudioNativeSoundEngine(application)
    val narratorEngine = NarratorEngine(application)

    // إدارة التحديثات التلقائية للتطبيق (Direct Auto-Update)
    val updateManager = AppUpdateManager(application)
    private val _updateInfo = MutableStateFlow<UpdateInfo?>(null)
    val updateInfo: StateFlow<UpdateInfo?> = _updateInfo.asStateFlow()
    val downloadState: StateFlow<DownloadState> = updateManager.downloadState
    private val _showUpdateDialog = MutableStateFlow(false)
    val showUpdateDialog: StateFlow<Boolean> = _showUpdateDialog.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val cloudStatus: StateFlow<CloudConnectionStatus> = repository.cloudStatus
    val onlineLeaderboard: StateFlow<List<OnlineLeaderboardPlayer>> = repository.onlineLeaderboard

    private val _currentScreen = MutableStateFlow(Screen.AUTH)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _availableAccounts = MutableStateFlow<List<CloudAccountOption>>(emptyList())
    val availableAccounts: StateFlow<List<CloudAccountOption>> = _availableAccounts.asStateFlow()

    private val _showAccountChooser = MutableStateFlow(false)
    val showAccountChooser: StateFlow<Boolean> = _showAccountChooser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _battleState = MutableStateFlow(BattleState())
    val battleState: StateFlow<BattleState> = _battleState.asStateFlow()

    private val _battleHistory = MutableStateFlow<List<BattleRecordEntity>>(emptyList())
    val battleHistory: StateFlow<List<BattleRecordEntity>> = _battleHistory.asStateFlow()

    private var enemyAiJob: Job? = null
    private var enemyTelegraphJob: Job? = null

    init {
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                if (user != null && _currentScreen.value == Screen.AUTH) {
                    _currentScreen.value = Screen.HOME
                    narratorEngine.speak("مرحباً بك يا ${user.username} في صدى المعركة. متصل بقاعدة بيانات فايربيز من جوجل بنجاح ومكتبة المؤثرات الصوتية الاحترافية جاهزة.")
                } else if (user == null && _currentScreen.value != Screen.AUTH) {
                    _currentScreen.value = Screen.AUTH
                }
            }
        }

        // فحص سرعة الاتصال بقاعدة بيانات فايربيز دورياً
        viewModelScope.launch {
            while (true) {
                delay(15000)
                repository.checkCloudPing()
            }
        }

        // فحص التحديثات التلقائية عند فتح التطبيق من مستودع GitHub
        checkForAppUpdates(manual = false)
    }

    // ==========================================
    // مصادقة الحسابات السحابية أونلاين واختيار الحساب
    // ==========================================

    fun openAccountChooser() {
        audioEngine.playUiClick()
        viewModelScope.launch {
            _authLoading.value = true
            val accounts = repository.getAvailableAccounts()
            _availableAccounts.value = accounts
            _authLoading.value = false
            _showAccountChooser.value = true
            narratorEngine.speak("تم جلب قائمة الحسابات من قاعدة بيانات فايربيز بنجاح. اختر حسابك من القائمة للدخول فوراً.")
        }
    }

    fun dismissAccountChooser() {
        audioEngine.playUiClick()
        _showAccountChooser.value = false
    }

    fun selectAccount(account: CloudAccountOption) {
        audioEngine.playAccountSelect()
        viewModelScope.launch {
            _showAccountChooser.value = false
            _authLoading.value = true
            _authError.value = null
            val res = repository.loginWithGoogle(account.email, account.realUsername)
            _authLoading.value = false
            res.onSuccess {
                audioEngine.playLoginSuccess()
                narratorEngine.speak("تم تسجيل الدخول بنجاح بحسابك عبر فايربيز: ${it.username}!")
                _currentScreen.value = Screen.HOME
            }.onFailure {
                _authError.value = it.message ?: "خطأ في تسجيل الدخول عبر فايربيز"
                narratorEngine.speak("فشل تسجيل الدخول: ${it.message}")
            }
        }
    }

    fun loginWithEmail(email: String, pass: String) {
        audioEngine.playUiClick()
        if (email.isBlank() || pass.isBlank()) {
            _authError.value = "يرجى كتابة البريد وكلمة المرور"
            narratorEngine.speak("يرجى كتابة البريد وكلمة المرور")
            return
        }
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            val res = repository.loginWithEmail(email, pass)
            _authLoading.value = false
            res.onSuccess {
                audioEngine.playLoginSuccess()
                narratorEngine.speak("تم تسجيل الدخول في قاعدة بيانات فايربيز بنجاح! أهلاً بك يا ${it.username}")
                _currentScreen.value = Screen.HOME
            }.onFailure {
                _authError.value = it.message ?: "خطأ في تسجيل الدخول عبر فايربيز"
                narratorEngine.speak("خطأ في تسجيل الدخول: ${it.message}")
            }
        }
    }

    fun registerWithEmail(name: String, email: String, pass: String) {
        audioEngine.playUiClick()
        if (email.isBlank() || pass.isBlank()) {
            _authError.value = "يرجى ملء الحقول المطلوبة"
            narratorEngine.speak("يرجى ملء الحقول المطلوبة")
            return
        }
        val realName = if (name.isNotBlank()) name.trim() else email.trim().substringBefore("@")
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            val res = repository.registerWithEmail(realName, email, pass)
            _authLoading.value = false
            res.onSuccess {
                audioEngine.playLoginSuccess()
                narratorEngine.speak("تم إنشاء الحساب في قاعدة بيانات فايربيز باسم ${it.username}! وتم منحك 600 عملة ذهبية.")
                _currentScreen.value = Screen.HOME
            }.onFailure {
                _authError.value = it.message ?: "فشل إنشاء الحساب في فايربيز"
                narratorEngine.speak("فشل إنشاء الحساب: ${it.message}")
            }
        }
    }

    fun loginWithGoogle(accountName: String = "haderrhadrr1234") {
        audioEngine.playAccountSelect()
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            val email = if (accountName.contains("@")) accountName.trim().lowercase() else "${accountName.trim().lowercase()}@gmail.com"
            val realName = email.substringBefore("@")
            val res = repository.loginWithGoogle(email, realName)
            _authLoading.value = false
            res.onSuccess {
                audioEngine.playLoginSuccess()
                narratorEngine.speak("تم تسجيل الدخول بحساب Google الحقيقي: ${it.username}!")
                _currentScreen.value = Screen.HOME
            }.onFailure {
                _authError.value = it.message
                narratorEngine.speak("فشل الدخول بحساب جوجل السحابي")
            }
        }
    }

    fun loginAsGuest() {
        audioEngine.playUiClick()
        viewModelScope.launch {
            _authLoading.value = true
            val res = repository.loginAsGuest()
            _authLoading.value = false
            res.onSuccess {
                audioEngine.playLoginSuccess()
                narratorEngine.speak("تم إنشاء جلسة لاعب جديدة في فايربيز للاسم: ${it.username}.")
                _currentScreen.value = Screen.HOME
            }
        }
    }

    fun logout() {
        audioEngine.playUiClick()
        viewModelScope.launch {
            repository.logout()
            _currentScreen.value = Screen.AUTH
            narratorEngine.speak("تم تسجيل الخروج ومزامنة التقدم مع فايربيز.")
        }
    }

    // ==========================================
    // التنقل وإدارة الشاشات
    // ==========================================

    fun navigateTo(screen: Screen) {
        audioEngine.playUiClick()
        _currentScreen.value = screen
        when (screen) {
            Screen.HOME -> narratorEngine.speak("الرئيسية أونلاين. رصيدك: ${currentUser.value?.gold ?: 0} ذهبة. متصل بقاعدة بيانات فايربيز.")
            Screen.WEAPONS -> narratorEngine.speak("ترسانة الأسلحة الاحترافية. ثلاثون سلاحاً بمؤثرات صوتية حصرية من مكتبة كيني ستوديو العالمية الأصلية للأندرويد.")
            Screen.SUPERPOWERS -> narratorEngine.speak("خزينة القوى الخارقة الاحترافية. استمع للأصوات وجهز قدرتك الساحقة.")
            Screen.HISTORY -> {
                loadHistory()
                narratorEngine.speak("سجل المعارك المحفوظ في فايربيز.")
            }
            Screen.LEADERBOARD -> {
                narratorEngine.speak("قائمة المتصدرين العالمية المزامنة مع فايربيز. استمع لترتيب أبطال اللعبة حول العالم.")
            }
            Screen.BATTLE -> startNewBattle()
            Screen.AUTH -> narratorEngine.speak("شاشة الدخول إلى قاعدة بيانات فايربيز.")
        }
    }

    fun loadHistory() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.getBattleHistory(user.id).collect {
                _battleHistory.value = it
            }
        }
    }

    // ==========================================
    // إدارة الأسلحة والقوى الخارقة
    // ==========================================

    fun previewWeapon(weapon: Weapon) {
        audioEngine.playWeaponSound(weapon.soundProfile)
        narratorEngine.speak("${weapon.name}. ${weapon.description}. قوة الضرر: ${weapon.damage}.")
    }

    fun equipWeapon(weapon: Weapon) {
        val user = currentUser.value ?: return
        val unlockedList = user.unlockedWeaponIds.split(",")
        if (unlockedList.contains(weapon.id.toString())) {
            viewModelScope.launch {
                repository.equipWeapon(weapon.id)
                audioEngine.playWeaponSound(weapon.soundProfile)
                narratorEngine.speak("تم تجهيز ${weapon.name} وحفظ الاختيار سحابياً.")
            }
        } else {
            if (user.gold >= weapon.costGold) {
                viewModelScope.launch {
                    val ok = repository.unlockWeapon(weapon.id, weapon.costGold)
                    if (ok) {
                        repository.equipWeapon(weapon.id)
                        audioEngine.playWeaponSound(weapon.soundProfile)
                        narratorEngine.speak("تم شراء وتجهيز ${weapon.name} وتحديث رصيدك السحابي بنجاح!")
                    }
                }
            } else {
                narratorEngine.speak("الذهب غير كافٍ. يتطلب ${weapon.costGold} ذهبة ولديك ${user.gold}.")
            }
        }
    }

    fun previewSuperpower(sp: Superpower) {
        audioEngine.playSuperpowerSound(sp.soundProfile)
        narratorEngine.speak("${sp.name}. ${sp.description}. قوة الضرر: ${sp.damage}. استهلاك الطاقة: ${sp.energyCost}.")
    }

    fun equipSuperpower(sp: Superpower) {
        val user = currentUser.value ?: return
        val unlockedList = user.unlockedSuperpowerIds.split(",")
        if (unlockedList.contains(sp.id.toString())) {
            viewModelScope.launch {
                repository.equipSuperpower(sp.id)
                audioEngine.playSuperpowerSound(sp.soundProfile)
                narratorEngine.speak("تم تجهيز القوة الخارقة: ${sp.name} في ملفك السحابي.")
            }
        } else {
            if (user.gold >= sp.costGold) {
                viewModelScope.launch {
                    val ok = repository.unlockSuperpower(sp.id, sp.costGold)
                    if (ok) {
                        repository.equipSuperpower(sp.id)
                        audioEngine.playSuperpowerSound(sp.soundProfile)
                        narratorEngine.speak("تم فتح وتجهيز القوة الخارقة ${sp.name} سحابياً!")
                    }
                }
            } else {
                narratorEngine.speak("الذهب غير كافٍ. يتطلب ${sp.costGold} ذهبة ولديك ${user.gold}.")
            }
        }
    }

    // ==========================================
    // حلبة المعارك الأونلاين (Online Sound Arena)
    // ==========================================

    private val onlineOpponents = listOf(
        "سيد الفراغ الأيوني (أونلاين)",
        "مدمر الكواكب الشبحية (منافس سحابي)",
        "صائد الترددات الكونية (مبارز عالمي)",
        "سيد العواصف المغناطيسية",
        "فارس الكسوف المظلم",
        "تنين البلازما المتوهج"
    )

    fun startNewBattle() {
        enemyAiJob?.cancel()
        val opponent = onlineOpponents.random()
        val user = currentUser.value
        val playerWeapon = WeaponsCatalog.getWeaponById(user?.equippedWeaponId ?: 1)
        val playerPower = SuperpowersCatalog.getSuperpowerById(user?.equippedSuperpowerId ?: 1)

        val monsterWeaponId = Random.nextInt(1, WeaponsCatalog.allWeapons.size + 1)
        val monsterPowerId = Random.nextInt(1, SuperpowersCatalog.allSuperpowers.size + 1)
        val monsterWeapon = WeaponsCatalog.getWeaponById(monsterWeaponId)
        val monsterPower = SuperpowersCatalog.getSuperpowerById(monsterPowerId)

        _battleState.value = BattleState(
            inBattle = true,
            currentTurn = BattleTurn.PLAYER_TURN,
            turnNumber = 1,
            isActionInProgress = false,
            opponentName = opponent,
            opponentMaxHp = 290 + Random.nextInt(40),
            opponentHp = 290,
            opponentEnergy = 35,
            opponentWeaponId = monsterWeaponId,
            opponentSuperpowerId = monsterPowerId,
            playerMaxHp = 300,
            playerHp = 300,
            playerEnergy = 40,
            isPlayerDefending = false,
            battleLog = listOf(
                "بدأت معركة الأدوار! سلاحك: [${playerWeapon.name}]، وقوتك: [${playerPower.name}].",
                "سلاح الوحش: [${monsterWeapon.name}]، وقوة الوحش: [${monsterPower.name}]."
            ),
            isBattleOver = false
        )

        audioEngine.playPlayerTurnStart()
        narratorEngine.speak(
            "بدأت معركة الأدوار ضد $opponent! إنه دورك الآن. " +
                    "سلاحك: ${playerWeapon.name}، وقوتك: ${playerPower.name}. " +
                    "سلاح الوحش: ${monsterWeapon.name}، وقوته: ${monsterPower.name}. " +
                    "اختر: الهجوم بسلاحك، أو إطلاق قوتك الخارقة، أو تفعيل درع الصد والدفاع."
        )
    }

    fun onPlayerAttack() {
        val state = _battleState.value
        if (state.isBattleOver || !state.inBattle || state.currentTurn != BattleTurn.PLAYER_TURN || state.isActionInProgress) return

        _battleState.value = state.copy(isActionInProgress = true)

        val user = currentUser.value
        val weapon = WeaponsCatalog.getWeaponById(user?.equippedWeaponId ?: 1)

        audioEngine.playWeaponSound(weapon.soundProfile)

        val variation = Random.nextInt(-8, 14)
        val finalDamage = ((weapon.damage + variation) * weapon.speed).toInt().coerceAtLeast(25)
        val newOpponentHp = (state.opponentHp - finalDamage).coerceAtLeast(0)
        val newEnergy = (state.playerEnergy + 20).coerceAtMost(100)
        val isVictory = newOpponentHp <= 0

        val log = "الجولة ${state.turnNumber} [دورك]: ضربت الوحش بسلاحك [${weapon.name}] وألحقت به $finalDamage ضرراً!"
        narratorEngine.speak("في دورك: ضربة بسلاحك ${weapon.name}! $finalDamage ضرر. صحة الوحش $newOpponentHp.")

        _battleState.value = state.copy(
            opponentHp = newOpponentHp,
            playerEnergy = newEnergy,
            battleLog = listOf(log) + state.battleLog.take(6),
            isBattleOver = isVictory,
            isVictory = isVictory
        )

        if (isVictory) {
            endBattle(isVictory = true)
        } else {
            triggerEnemyTurn()
        }
    }

    fun onPlayerSuperpower() {
        val state = _battleState.value
        if (state.isBattleOver || !state.inBattle || state.currentTurn != BattleTurn.PLAYER_TURN || state.isActionInProgress) return

        val user = currentUser.value
        val power = SuperpowersCatalog.getSuperpowerById(user?.equippedSuperpowerId ?: 1)

        if (state.playerEnergy < power.energyCost) {
            narratorEngine.speak("طاقة غير كافية! تتطلب القوة ${power.energyCost}%، وطاقتك الحالية ${state.playerEnergy}%. اضرب بالسلاح في دورك لشحن الطاقة.")
            return
        }

        _battleState.value = state.copy(isActionInProgress = true)

        audioEngine.playSuperpowerSound(power.soundProfile)

        val variation = Random.nextInt(0, 25)
        val finalDamage = power.damage + variation
        val newOpponentHp = (state.opponentHp - finalDamage).coerceAtLeast(0)
        val newEnergy = (state.playerEnergy - power.energyCost).coerceAtLeast(0)
        val isVictory = newOpponentHp <= 0

        val log = "الجولة ${state.turnNumber} [دورك]: أطلقت قوتك الخارقة [${power.name}]! دمار بقوة $finalDamage!"
        narratorEngine.speak("في دورك: تفجير قوتك الخارقة ${power.name}! $finalDamage ضرر ساحق. صحة الوحش $newOpponentHp.")

        _battleState.value = state.copy(
            opponentHp = newOpponentHp,
            playerEnergy = newEnergy,
            battleLog = listOf(log) + state.battleLog.take(6),
            isBattleOver = isVictory,
            isVictory = isVictory
        )

        if (isVictory) {
            endBattle(isVictory = true)
        } else {
            triggerEnemyTurn()
        }
    }

    fun onPlayerDefend() {
        val state = _battleState.value
        if (state.isBattleOver || !state.inBattle || state.currentTurn != BattleTurn.PLAYER_TURN || state.isActionInProgress) return

        _battleState.value = state.copy(isActionInProgress = true)

        audioEngine.playDefenseSound()
        val newEnergy = (state.playerEnergy + 20).coerceAtMost(100)

        val log = "الجولة ${state.turnNumber} [دورك]: قمت بتفعيل درع الصد والدفاع لتقليص ضرر الوحش بنسبة 70%!"
        narratorEngine.speak("في دورك: تم تفعيل درع الصد والدفاع! ستتلقى ضرراً مخفضاً في دور الوحش القادم.")

        _battleState.value = state.copy(
            isPlayerDefending = true,
            playerEnergy = newEnergy,
            battleLog = listOf(log) + state.battleLog.take(6)
        )

        triggerEnemyTurn()
    }

    private fun triggerEnemyTurn() {
        enemyAiJob?.cancel()
        enemyAiJob = viewModelScope.launch {
            _battleState.value = _battleState.value.copy(
                currentTurn = BattleTurn.ENEMY_TURN,
                isActionInProgress = true
            )

            delay(1000)
            if (_battleState.value.isBattleOver) return@launch

            audioEngine.playEnemyTurnStart()
            narratorEngine.speak("انتهى دورك! الآن دور الوحش ${_battleState.value.opponentName}.")

            delay(1200)
            if (_battleState.value.isBattleOver) return@launch

            executeEnemyTurn()
        }
    }

    private fun executeEnemyTurn() {
        val state = _battleState.value
        if (state.isBattleOver || !state.inBattle) return

        val monsterWeapon = WeaponsCatalog.getWeaponById(state.opponentWeaponId)
        val monsterPower = SuperpowersCatalog.getSuperpowerById(state.opponentSuperpowerId)

        val useSuperpower = state.opponentEnergy >= monsterPower.energyCost && (Random.nextFloat() > 0.4f || state.opponentHp < 100)

        val rawDamage: Int
        val actionDescription: String

        if (useSuperpower) {
            audioEngine.playMonsterSuperpower()
            audioEngine.playSuperpowerSound(monsterPower.soundProfile)
            rawDamage = monsterPower.damage + Random.nextInt(-5, 18)
            actionDescription = "أطلق قوته الخارقة [${monsterPower.name}]"
        } else {
            audioEngine.playMonsterAttack()
            audioEngine.playWeaponSound(monsterWeapon.soundProfile)
            rawDamage = ((monsterWeapon.damage + Random.nextInt(-6, 12)) * monsterWeapon.speed).toInt().coerceAtLeast(24)
            actionDescription = "هاجم بسلاحه [${monsterWeapon.name}]"
        }

        val newOpponentEnergy = if (useSuperpower) {
            (state.opponentEnergy - monsterPower.energyCost).coerceAtLeast(0)
        } else {
            (state.opponentEnergy + 25).coerceAtMost(100)
        }

        val actualDamage: Int
        val logMsg: String

        if (state.isPlayerDefending) {
            actualDamage = (rawDamage * 0.30f).toInt().coerceAtLeast(6)
            audioEngine.playParrySound()
            logMsg = "الجولة ${state.turnNumber} [دور الوحش]: $actionDescription لكنك صددت الضربة بدرعك وتلقيت $actualDamage ضرر فقط!"
            narratorEngine.speak("في دور الوحش: $actionDescription! صد ممتاز بدرعك! تلقيت فقط $actualDamage نقطة.")
        } else {
            actualDamage = rawDamage
            audioEngine.playHitSound()
            logMsg = "الجولة ${state.turnNumber} [دور الوحش]: $actionDescription وأصابك مباشرة محدثاً $actualDamage ضرراً!"
            narratorEngine.speak("في دور الوحش: $actionDescription! أصابك الهجوم وخسرت $actualDamage نقطة حياة.")
        }

        val newPlayerHp = (state.playerHp - actualDamage).coerceAtLeast(0)
        val isGameOver = newPlayerHp <= 0

        _battleState.value = state.copy(
            playerHp = newPlayerHp,
            opponentEnergy = newOpponentEnergy,
            opponentLastAction = actionDescription,
            isPlayerDefending = false,
            battleLog = listOf(logMsg) + state.battleLog.take(6),
            isBattleOver = isGameOver,
            isVictory = false
        )

        if (isGameOver) {
            endBattle(isVictory = false)
        } else {
            viewModelScope.launch {
                delay(1200)
                if (_battleState.value.isBattleOver) return@launch

                val nextTurnNum = state.turnNumber + 1
                _battleState.value = _battleState.value.copy(
                    currentTurn = BattleTurn.PLAYER_TURN,
                    turnNumber = nextTurnNum,
                    isActionInProgress = false
                )

                audioEngine.playPlayerTurnStart()
                narratorEngine.speak("بدأ دورك للجولة $nextTurnNum! صحتك $newPlayerHp وصحة الوحش ${state.opponentHp}. اختر حركتك.")
            }
        }
    }

    fun speakBattleStatus() {
        val state = _battleState.value
        val user = currentUser.value
        val playerWeapon = WeaponsCatalog.getWeaponById(user?.equippedWeaponId ?: 1)
        val playerPower = SuperpowersCatalog.getSuperpowerById(user?.equippedSuperpowerId ?: 1)
        val monsterWeapon = WeaponsCatalog.getWeaponById(state.opponentWeaponId)
        val monsterPower = SuperpowersCatalog.getSuperpowerById(state.opponentSuperpowerId)

        val turnStr = if (state.currentTurn == BattleTurn.PLAYER_TURN) "دورك الحالي" else "دور الوحش"

        narratorEngine.speak(
            "حالة المعركة بنظام الأدوار: الجولة رقم ${state.turnNumber}. $turnStr. " +
                    "صحتك ${state.playerHp} من ${state.playerMaxHp}، وطاقتك ${state.playerEnergy}%. " +
                    "سلاحك: ${playerWeapon.name}، وقوتك: ${playerPower.name}. " +
                    "صحة الوحش ${state.opponentHp} من ${state.opponentMaxHp}، وطاقته ${state.opponentEnergy}%. " +
                    "سلاح الوحش: ${monsterWeapon.name}، وقوته: ${monsterPower.name}."
        )
    }

    private fun endBattle(isVictory: Boolean) {
        enemyAiJob?.cancel()
        enemyTelegraphJob?.cancel()

        val user = currentUser.value
        val weapon = WeaponsCatalog.getWeaponById(user?.equippedWeaponId ?: 1)
        val power = SuperpowersCatalog.getSuperpowerById(user?.equippedSuperpowerId ?: 1)

        val goldWon = if (isVictory) 140 else 50
        val xpWon = if (isVictory) 160 else 60

        if (isVictory) {
            audioEngine.playVictorySound()
            narratorEngine.speak("نصر مؤزر! لقد سحقت خصمك ${battleState.value.opponentName}! تمت مزامنة نصرك مع فايربيز بنجاح.")
        } else {
            audioEngine.playDefeatSound()
            narratorEngine.speak("سقطت في المعركة. حظاً أوفر في المرة القادمة. تم تسجيل النتيجة في فايربيز.")
        }

        _battleState.value = _battleState.value.copy(
            goldEarned = goldWon,
            xpEarned = xpWon
        )

        viewModelScope.launch {
            repository.recordBattleResult(
                isVictory = isVictory,
                opponentName = _battleState.value.opponentName,
                weaponName = weapon.name,
                superpowerName = power.name,
                damageDealt = _battleState.value.opponentMaxHp - _battleState.value.opponentHp,
                damageTaken = _battleState.value.playerMaxHp - _battleState.value.playerHp
            )
        }
    }

    // ==========================================
    // إدارة التحديثات التلقائية (Seamless Direct Auto Update)
    // ==========================================

    fun checkForAppUpdates(manual: Boolean = false) {
        viewModelScope.launch {
            if (manual) {
                narratorEngine.speak("جاري فحص تحديثات اللعبة تلقائياً...")
            }
            val result = updateManager.checkForUpdates()
            result.onSuccess { info ->
                _updateInfo.value = info
                if (info.isUpdateAvailable) {
                    _showUpdateDialog.value = true
                    audioEngine.playAccountSelect()
                    narratorEngine.speak("يتوفر تحديث جديد للعبة! الإصدار ${info.latestVersionName}. هل تريد التحديث الآن؟")
                } else if (manual) {
                    narratorEngine.speak("لعبتك محدثة إلى أحدث إصدار: ${updateManager.currentVersionName}")
                }
            }.onFailure { e ->
                android.util.Log.w("GameViewModel", "Update check failed", e)
                if (manual) {
                    narratorEngine.speak("تعذر الاتصال بخادم التحديثات حالياً. تأكد من اتصال الإنترنت.")
                }
            }
        }
    }

    fun startAppUpdate() {
        val info = _updateInfo.value ?: return
        audioEngine.playUiClick()
        narratorEngine.speak("جاري تنزيل ملف التحديث وتثبيته الآن...")
        viewModelScope.launch {
            updateManager.downloadAndInstall(info.apkDownloadUrl)
        }
    }

    fun dismissUpdateDialog() {
        audioEngine.playUiClick()
        _showUpdateDialog.value = false
        updateManager.resetDownloadState()
        narratorEngine.speak("تم إغلاق نافذة التحديث.")
    }

    override fun onCleared() {
        super.onCleared()
        enemyAiJob?.cancel()
        enemyTelegraphJob?.cancel()
        audioEngine.release()
        narratorEngine.shutdown()
    }
}

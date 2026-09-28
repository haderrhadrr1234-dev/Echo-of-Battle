package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val email: String,
    val passwordHash: String,
    val authProvider: String, // "EMAIL", "GOOGLE", "GUEST"
    val level: Int = 1,
    val xp: Int = 0,
    val gold: Int = 300,
    val wins: Int = 0,
    val losses: Int = 0,
    val equippedWeaponId: Int = 1,
    val equippedSuperpowerId: Int = 1,
    val unlockedWeaponIds: String = "1",
    val unlockedSuperpowerIds: String = "1",
    val isCurrentSession: Boolean = false
)

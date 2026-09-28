package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "battle_records")
data class BattleRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val opponentName: String,
    val result: String, // "VICTORY", "DEFEAT"
    val weaponUsed: String,
    val superpowerUsed: String,
    val damageDealt: Int,
    val damageTaken: Int,
    val goldEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)

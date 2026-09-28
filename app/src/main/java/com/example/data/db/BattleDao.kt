package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BattleDao {
    @Query("SELECT * FROM battle_records WHERE userId = :userId ORDER BY timestamp DESC")
    fun getBattleHistoryForUser(userId: Long): Flow<List<BattleRecordEntity>>

    @Insert
    suspend fun insertRecord(record: BattleRecordEntity): Long

    @Query("DELETE FROM battle_records WHERE userId = :userId")
    suspend fun clearHistoryForUser(userId: Long)
}

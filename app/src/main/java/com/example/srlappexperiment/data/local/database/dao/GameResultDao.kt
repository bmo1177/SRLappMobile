package com.example.srlappexperiment.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.srlappexperiment.data.local.database.entities.GameResult
import kotlinx.coroutines.flow.Flow

/**
 * DAO for GameResult entity
 */
@Dao
interface GameResultDao {

    @Insert
    suspend fun insert(result: GameResult): Long

    @Query("SELECT * FROM game_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<GameResult>>

    @Query("SELECT * FROM game_results ORDER BY score DESC LIMIT 10")
    fun getLeaderboard(): Flow<List<GameResult>>

    @Query("SELECT MAX(score) FROM game_results WHERE difficulty = :difficulty")
    fun getPersonalBest(difficulty: String): Flow<Int?>
}

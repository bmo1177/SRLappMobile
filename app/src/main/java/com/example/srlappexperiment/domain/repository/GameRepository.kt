package com.example.srlappexperiment.domain.repository

import com.example.srlappexperiment.data.local.database.entities.GameResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for game-related data
 */
interface GameRepository {
    suspend fun saveGameResult(result: GameResult): Long
    fun getLeaderboard(): Flow<List<GameResult>>
    fun getPersonalBest(difficulty: String): Flow<Int?>
    fun getAllResults(): Flow<List<GameResult>>
}

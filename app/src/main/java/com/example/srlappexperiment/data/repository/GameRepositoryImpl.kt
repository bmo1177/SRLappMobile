package com.example.srlappexperiment.data.repository

import com.example.srlappexperiment.data.local.database.dao.GameResultDao
import com.example.srlappexperiment.data.local.database.entities.GameResult
import com.example.srlappexperiment.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val gameResultDao: GameResultDao
) : GameRepository {

    override suspend fun saveGameResult(result: GameResult): Long {
        return gameResultDao.insert(result)
    }

    override fun getLeaderboard(): Flow<List<GameResult>> {
        return gameResultDao.getLeaderboard()
    }

    override fun getPersonalBest(difficulty: String): Flow<Int?> {
        return gameResultDao.getPersonalBest(difficulty)
    }

    override fun getAllResults(): Flow<List<GameResult>> {
        return gameResultDao.getAllResults()
    }
}

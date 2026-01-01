package com.example.srlappexperiment.data.repository

import com.example.srlappexperiment.data.local.database.dao.DailyProgressDao
import com.example.srlappexperiment.data.local.database.entities.DailyProgress
import com.example.srlappexperiment.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository implementation for managing daily progress and streaks
 */
class ProgressRepositoryImpl @Inject constructor(
    private val dailyProgressDao: DailyProgressDao
) : ProgressRepository {

    override suspend fun updateDailyProgress(progress: DailyProgress) {
        dailyProgressDao.insert(progress)
    }

    override fun getByDate(date: String): Flow<DailyProgress?> {
        return dailyProgressDao.getByDate(date)
    }

    override fun getProgressRange(startDate: String, endDate: String): Flow<List<DailyProgress>> {
        return dailyProgressDao.getProgressRange(startDate, endDate)
    }

    override fun getCurrentStreak(): Flow<Int> {
        return dailyProgressDao.getCurrentStreak().map { it ?: 0 }
    }

    override fun getLongestStreak(): Flow<Int> {
        return dailyProgressDao.getLongestStreak().map { it ?: 0 }
    }
}

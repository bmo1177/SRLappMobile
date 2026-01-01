package com.example.srlappexperiment.domain.repository

import com.example.srlappexperiment.data.local.database.entities.DailyProgress
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for daily progress and streak tracking
 */
interface ProgressRepository {
    suspend fun updateDailyProgress(progress: DailyProgress)
    fun getByDate(date: String): Flow<DailyProgress?>
    fun getProgressRange(startDate: String, endDate: String): Flow<List<DailyProgress>>
    fun getCurrentStreak(): Flow<Int>
    fun getLongestStreak(): Flow<Int>
}

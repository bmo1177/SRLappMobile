package com.example.srlappexperiment.domain.repository

import com.example.srlappexperiment.data.local.database.entities.StudySession
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for study session operations and statistics
 */
interface StudySessionRepository {
    suspend fun saveSession(session: StudySession): Long
    fun getSessionsInRange(startDate: Long, endDate: Long): Flow<List<StudySession>>
    fun getTotalStudyTimeMinutes(): Flow<Int>
    fun getAverageAccuracy(): Flow<Float>
    fun getSessionCount(): Flow<Int>
}

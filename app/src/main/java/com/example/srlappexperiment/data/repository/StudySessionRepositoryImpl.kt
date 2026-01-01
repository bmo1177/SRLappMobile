package com.example.srlappexperiment.data.repository

import com.example.srlappexperiment.data.local.database.dao.StudySessionDao
import com.example.srlappexperiment.data.local.database.entities.StudySession
import com.example.srlappexperiment.domain.repository.StudySessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository implementation for managing study sessions
 */
class StudySessionRepositoryImpl @Inject constructor(
    private val studySessionDao: StudySessionDao
) : StudySessionRepository {

    override suspend fun saveSession(session: StudySession): Long {
        return studySessionDao.insert(session)
    }

    override fun getSessionsInRange(startDate: Long, endDate: Long): Flow<List<StudySession>> {
        return studySessionDao.getSessionsInRange(startDate, endDate)
    }

    override fun getTotalStudyTimeMinutes(): Flow<Int> {
        return studySessionDao.getTotalStudyTime().map { it ?: 0 }
    }

    override fun getAverageAccuracy(): Flow<Float> {
        return studySessionDao.getAverageAccuracy().map { it ?: 0f }
    }

    override fun getSessionCount(): Flow<Int> {
        return studySessionDao.getSessionCount()
    }
}

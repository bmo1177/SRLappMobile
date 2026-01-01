package com.example.srlappexperiment.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.srlappexperiment.data.local.database.entities.StudySession
import kotlinx.coroutines.flow.Flow

/**
 * DAO for StudySession entity
 */
@Dao
interface StudySessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: StudySession): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<StudySession>)

    @Query("SELECT * FROM study_sessions WHERE startTime >= :dateStart AND startTime < :dateEnd ORDER BY startTime DESC")
    fun getSessionsByDate(dateStart: Long, dateEnd: Long): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE startTime >= :startDate AND startTime <= :endDate ORDER BY startTime DESC")
    fun getSessionsInRange(startDate: Long, endDate: Long): Flow<List<StudySession>>

    @Query("SELECT SUM(durationMinutes) FROM study_sessions")
    fun getTotalStudyTime(): Flow<Int?>

    @Query("SELECT AVG(accuracyPercentage) FROM study_sessions WHERE wordsReviewed > 0")
    fun getAverageAccuracy(): Flow<Float?>

    @Query("SELECT * FROM study_sessions WHERE synced = 0")
    fun getUnsyncedSessions(): Flow<List<StudySession>>

    @Query("SELECT COUNT(*) FROM study_sessions")
    fun getSessionCount(): Flow<Int>

    @Query("UPDATE study_sessions SET synced = 1 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<Long>)
}


package com.example.srlappexperiment.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.srlappexperiment.data.local.database.entities.DailyProgress
import kotlinx.coroutines.flow.Flow

/**
 * DAO for DailyProgress entity
 */
@Dao
interface DailyProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: DailyProgress)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(progressList: List<DailyProgress>)

    @Update
    suspend fun update(progress: DailyProgress)

    @Query("SELECT * FROM daily_progress WHERE date = :date")
    fun getByDate(date: String): Flow<DailyProgress?>

    @Query("SELECT * FROM daily_progress WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getProgressRange(startDate: String, endDate: String): Flow<List<DailyProgress>>

    @Query("SELECT dailyStreak FROM daily_progress ORDER BY date DESC LIMIT 1")
    fun getCurrentStreak(): Flow<Int?>

    @Query("SELECT MAX(dailyStreak) FROM daily_progress")
    fun getLongestStreak(): Flow<Int?>

    @Query("SELECT * FROM daily_progress WHERE synced = 0")
    fun getUnsyncedProgress(): Flow<List<DailyProgress>>

    @Query("UPDATE daily_progress SET synced = 1 WHERE date IN (:dates)")
    suspend fun markSynced(dates: List<String>)
}


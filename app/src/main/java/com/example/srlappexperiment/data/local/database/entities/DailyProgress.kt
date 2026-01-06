package com.example.srlappexperiment.data.local.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Daily progress entity for tracking daily study statistics
 */
@Entity(
    tableName = "daily_progress",
    indices = [
        Index(value = ["date"]),
        Index(value = ["synced"])
    ]
)
data class DailyProgress(
    @PrimaryKey
    val date: String = "", // YYYY-MM-DD format
    val dailyStreak: Int = 0,
    val minutesStudied: Int = 0,
    val wordsReviewed: Int = 0,
    val wordsLearned: Int = 0,
    val accuracyPercentage: Float = 0f,
    val synced: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)


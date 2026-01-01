package com.example.srlappexperiment.data.local.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Study session entity for tracking user study activities
 */
@Entity(
    tableName = "study_sessions",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["sessionType"]),
        Index(value = ["synced"])
    ]
)
data class StudySession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val durationMinutes: Int = 0,
    val sessionType: String = "", // vocabulary/focus_game/ielts_practice
    val wordsReviewed: Int = 0,
    val accuracyPercentage: Float = 0f,
    val focusScore: Int? = null, // 1-10, nullable
    val synced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)


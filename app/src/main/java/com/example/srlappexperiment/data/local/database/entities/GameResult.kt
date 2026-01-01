package com.example.srlappexperiment.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for storing game results of the Focus Challenge
 */
@Entity(tableName = "game_results")
data class GameResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val score: Int = 0,
    val accuracy: Float = 0f,
    val questionsAnswered: Int = 0,
    val correctAnswers: Int = 0,
    val comboMax: Int = 0,
    val duration: Long = 90000L, // 90 seconds in ms
    val difficulty: String = "", // beginner/intermediate/advanced
    val timestamp: Long = System.currentTimeMillis()
)

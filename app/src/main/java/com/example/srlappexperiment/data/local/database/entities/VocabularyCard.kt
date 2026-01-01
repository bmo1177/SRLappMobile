package com.example.srlappexperiment.data.local.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Vocabulary card entity for spaced repetition learning
 * Uses SM-2 algorithm for spaced repetition
 */
@Entity(
    tableName = "vocabulary_cards",
    indices = [
        Index(value = ["nextReviewDate"]),
        Index(value = ["difficulty"]),
        Index(value = ["category"]),
        Index(value = ["synced"])
    ]
)
data class VocabularyCard(
    @PrimaryKey
    val id: String = "",
    val word: String = "",
    val translation: String = "",
    val definition: String = "",
    val exampleSentence: String = "",
    val pronunciation: String = "",
    val difficulty: String = "", // beginner/intermediate/advanced
    val category: String = "", // ielts_academic/toefl_essential
    val easeFactor: Float = 2.5f, // SM-2 algorithm easiness factor
    val interval: Int = 1, // Days until next review
    val nextReviewDate: Long = System.currentTimeMillis(), // Timestamp for next review
    val lastReviewDate: Long? = null,
    val timesReviewed: Int = 0,
    val timesCorrect: Int = 0,
    val timesIncorrect: Int = 0,
    val synced: Boolean = false, // Sync status with Firestore
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)


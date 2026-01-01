package com.example.srlappexperiment.domain.model

import androidx.compose.runtime.Stable

/**
 * Domain model for VocabularyCard
 * Note: Room entity is in data.local.database.entities.VocabularyCard
 */
@Stable
data class VocabularyCard(
    val id: String = "",
    val word: String = "",
    val definition: String = "",
    val example: String = "",
    val difficulty: String = "", // IELTS/TOEFL level
    val repetition: Int = 0, // Number of times card has been reviewed
    val easinessFactor: Double = 2.5, // SM-2 algorithm easiness factor
    val interval: Int = 1, // Days until next review
    val nextReviewDate: Long = System.currentTimeMillis(), // Timestamp for next review
    val lastReviewedDate: Long? = null,
    val isLearned: Boolean = false,
    val isSynced: Boolean = false, // Sync status with Firestore
    val userId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)


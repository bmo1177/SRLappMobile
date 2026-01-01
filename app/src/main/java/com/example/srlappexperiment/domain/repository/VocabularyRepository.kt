package com.example.srlappexperiment.domain.repository

import com.example.srlappexperiment.data.local.database.entities.StudySession
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for vocabulary-related data operations
 */
interface VocabularyRepository {
    
    /**
     * Get cards that are due for review
     * @param limit Maximum number of cards to return
     */
    fun getDueCards(limit: Int): Flow<List<VocabularyCard>>
    
    /**
     * Get new cards that haven't been reviewed yet
     * @param limit Maximum number of cards to return
     */
    fun getNewCards(limit: Int): Flow<List<VocabularyCard>>
    
    /**
     * Get all vocabulary cards
     */
    fun getAllCards(): Flow<List<VocabularyCard>>
    
    /**
     * Update a vocabulary card after review
     */
    suspend fun updateCard(card: VocabularyCard)
    
    /**
     * Save a completed study session
     */
    suspend fun saveStudySession(session: StudySession): Long
    
    /**
     * Get total count of cards in review queue (due today or overdue)
     */
    suspend fun getReviewQueueCount(): Int
    
    /**
     * Get total count of cards learned (reviewed at least once)
     */
    fun getTotalCardsLearned(): Flow<Int>
    
    /**
     * Get cards that haven't been synced to Firestore
     */
    fun getUnsyncedCards(): Flow<List<VocabularyCard>>
}

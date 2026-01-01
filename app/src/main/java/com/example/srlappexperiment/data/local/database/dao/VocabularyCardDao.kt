package com.example.srlappexperiment.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import kotlinx.coroutines.flow.Flow

/**
 * DAO for VocabularyCard entity
 */
@Dao
interface VocabularyCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: VocabularyCard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<VocabularyCard>)

    @Update
    suspend fun update(card: VocabularyCard)

    @Delete
    suspend fun delete(card: VocabularyCard)

    @Query("SELECT * FROM vocabulary_cards WHERE id = :id")
    fun getById(id: String): Flow<VocabularyCard?>

    @Query("SELECT * FROM vocabulary_cards WHERE nextReviewDate <= :now ORDER BY nextReviewDate ASC LIMIT :limit")
    fun getDueCards(now: Long, limit: Int): Flow<List<VocabularyCard>>

    @Query("SELECT * FROM vocabulary_cards WHERE timesReviewed = 0 ORDER BY createdAt ASC LIMIT :limit")
    fun getNewCards(limit: Int): Flow<List<VocabularyCard>>

    @Query("SELECT * FROM vocabulary_cards WHERE category = :category")
    fun getCardsByCategory(category: String): Flow<List<VocabularyCard>>

    @Query("SELECT * FROM vocabulary_cards WHERE difficulty = :difficulty")
    fun getCardsByDifficulty(difficulty: String): Flow<List<VocabularyCard>>

    @Query("SELECT * FROM vocabulary_cards WHERE synced = 0")
    fun getUnsyncedCards(): Flow<List<VocabularyCard>>

    @Query("SELECT COUNT(*) FROM vocabulary_cards WHERE timesReviewed > 0")
    fun getTotalCardsLearned(): Flow<Int>

    @Query("SELECT SUM(timesCorrect) FROM vocabulary_cards WHERE timesReviewed > 0")
    fun getTotalCorrect(): Flow<Long?>

    @Query("SELECT SUM(timesReviewed) FROM vocabulary_cards WHERE timesReviewed > 0")
    fun getTotalReviewed(): Flow<Long?>

    @Query("SELECT * FROM vocabulary_cards ORDER BY nextReviewDate ASC")
    fun getAllCards(): Flow<List<VocabularyCard>>

    @Query("UPDATE vocabulary_cards SET synced = 1 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>)
}


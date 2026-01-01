package com.example.srlappexperiment.domain.usecase

import com.example.srlappexperiment.data.local.database.dao.VocabularyCardDao
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import com.example.srlappexperiment.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.max

/**
 * SuperMemo-2 (SM-2) Spaced Repetition Algorithm Engine
 * 
 * The SM-2 algorithm optimizes learning intervals based on user performance.
 * 
 * Formula: EF' = max(1.3, EF + (0.1 - (5-q) × (0.08 + (5-q) × 0.02)))
 * Where:
 *   - EF = current ease factor (difficulty rating)
 *   - q = quality of recall (0-5 scale)
 *   - EF' = new ease factor
 * 
 * Quality Ratings:
 *   0-2 (Again/Poor): Complete failure, reset to 1 day
 *   3 (Hard): Barely remembered, reset to 1 day but continue progression
 *   4 (Good): Some effort to recall, interval increases normally
 *   5 (Easy): Instant recall, interval increases faster
 * 
 * Review Intervals:
 *   - First review: 1 day
 *   - Second review: 6 days (modified from original 3 days for language learning)
 *   - Third+ review: interval = previous_interval × EF
 * 
 * Example progression:
 *   - Day 1: First review (quality 4) → next review in 1 day
 *   - Day 2: Second review (quality 4) → next review in 6 days
 *   - Day 8: Third review (quality 4) → next review in ~15 days (6 × 2.5)
 *   - Day 23: Fourth review (quality 5) → next review in ~40 days (15 × 2.6)
 */
class SpacedRepetitionEngine @Inject constructor(
    private val vocabularyCardDao: VocabularyCardDao
) {

    /**
     * Reviews a vocabulary card and updates it based on the quality of recall
     * 
     * @param card The vocabulary card to review
     * @param quality Quality of recall (0-5):
     *   0-2: Complete failure (Again/Poor)
     *   3: Hard (barely remembered)
     *   4: Good (some effort)
     *   5: Easy (instant recall)
     * @return Updated vocabulary card with new ease factor, interval, and review date
     */
    fun reviewCard(card: VocabularyCard, quality: Int): VocabularyCard {
        require(quality in Constants.MIN_QUALITY_RATING..Constants.MAX_QUALITY_RATING) {
            "Quality must be between ${Constants.MIN_QUALITY_RATING} and ${Constants.MAX_QUALITY_RATING}"
        }

        val now = System.currentTimeMillis()
        val currentEF = card.easeFactor
        val currentInterval = card.interval
        val currentTimesReviewed = card.timesReviewed

        // Calculate new ease factor using SM-2 formula
        val newEF = calculateNewEaseFactor(currentEF, quality)

        // Determine new interval based on quality and current state
        val newInterval = calculateNewInterval(
            quality = quality,
            currentInterval = currentInterval,
            currentTimesReviewed = currentTimesReviewed,
            newEF = newEF
        )

        // Calculate next review date
        val nextReviewDate = calculateNextReviewDateInternal(now, newInterval)

        // Update statistics
        val newTimesReviewed = currentTimesReviewed + 1
        val (newTimesCorrect, newTimesIncorrect) = if (quality >= Constants.MIN_QUALITY_TO_CONTINUE) {
            card.timesCorrect + 1 to card.timesIncorrect
        } else {
            card.timesCorrect to card.timesIncorrect + 1
        }

        return card.copy(
            easeFactor = newEF,
            interval = newInterval,
            nextReviewDate = nextReviewDate,
            lastReviewDate = now,
            timesReviewed = newTimesReviewed,
            timesCorrect = newTimesCorrect,
            timesIncorrect = newTimesIncorrect,
            synced = false, // Mark as unsynced after review
            updatedAt = now
        )
    }

    /**
     * Gets cards that are due for review
     * 
     * @param limit Maximum number of cards to return (default: 30)
     * @return Flow of vocabulary cards sorted by next review date (overdue first)
     */
    suspend fun getDueCards(limit: Int = Constants.DEFAULT_DUE_CARDS_LIMIT): Flow<List<VocabularyCard>> {
        val now = System.currentTimeMillis()
        return vocabularyCardDao.getDueCards(now, limit)
    }

    /**
     * Gets new cards that haven't been reviewed yet
     * 
     * @param limit Maximum number of new cards to return (default: 10)
     * @return Flow of new vocabulary cards
     */
    suspend fun getNewCards(limit: Int = Constants.DEFAULT_NEW_CARDS_LIMIT): Flow<List<VocabularyCard>> {
        return vocabularyCardDao.getNewCards(limit)
    }

    /**
     * Marks a card as reviewed and updates it in the database
     * 
     * @param cardId ID of the card to mark as reviewed
     * @param quality Quality of recall (0-5)
     * @return Flow of Unit indicating completion
     */
    suspend fun markCardReviewed(cardId: String, quality: Int): Flow<Unit> {
        val cardFlow = vocabularyCardDao.getById(cardId)
        val card = cardFlow.first() ?: return flowOf(Unit)
        
        val updatedCard = reviewCard(card, quality)
        vocabularyCardDao.update(updatedCard)
        
        return flowOf(Unit)
    }

    /**
     * Calculates the next review date timestamp based on quality and current state
     * 
     * @param quality Quality of recall (0-5)
     * @param currentEF Current ease factor
     * @param currentInterval Current interval in days
     * @return Timestamp for next review date
     */
    fun calculateNextReviewDate(
        quality: Int,
        currentEF: Float,
        currentInterval: Int
    ): Long {
        val now = System.currentTimeMillis()
        // Use currentInterval and a mock timesReviewed of 2 to ensure we use the EF multiplication logic
        val newInterval = calculateNewInterval(quality, currentInterval, 2, currentEF)
        return calculateNextReviewDateInternal(now, newInterval)
    }

    /**
     * Calculates the new ease factor using the SM-2 formula
     * 
     * Formula: EF' = max(1.3, EF + (0.1 - (5-q) × (0.08 + (5-q) × 0.02)))
     * 
     * @param currentEF Current ease factor
     * @param quality Quality of recall (0-5)
     * @return New ease factor (minimum 1.3)
     */
    private fun calculateNewEaseFactor(currentEF: Float, quality: Int): Float {
        val fiveMinusQ = 5 - quality
        val calculation = 0.1f - (fiveMinusQ * (0.08f + (fiveMinusQ * 0.02f)))
        val newEF = currentEF + calculation
        return max(Constants.MIN_EASINESS_FACTOR, newEF)
    }

    /**
     * Calculates the new review interval in days
     * 
     * Rules:
     *   - Quality < 3: Reset to 1 day (complete failure)
     *   - Quality = 3: Reset to 1 day (hard, but continue progression)
     *   - First review (timesReviewed = 0): 1 day
     *   - Second review (timesReviewed = 1): 6 days
     *   - Third+ review (timesReviewed >= 2): interval = previous_interval × EF
     * 
     * @param quality Quality of recall (0-5)
     * @param currentInterval Current interval in days
     * @param currentTimesReviewed Current number of times reviewed
     * @param newEF New ease factor after review
     * @return New interval in days
     */
    private fun calculateNewInterval(
        quality: Int,
        currentInterval: Int,
        currentTimesReviewed: Int,
        newEF: Float
    ): Int {
        // Quality < 3: Complete failure, reset to 1 day
        if (quality < Constants.MIN_QUALITY_TO_CONTINUE) {
            return Constants.FIRST_REVIEW_INTERVAL_DAYS
        }

        // Quality = 3: Hard (barely remembered), reset to 1 day but continue progression
        if (quality == 3) {
            return Constants.FIRST_REVIEW_INTERVAL_DAYS
        }

        return when (currentTimesReviewed) {
            0 -> Constants.FIRST_REVIEW_INTERVAL_DAYS // First review: 1 day
            1 -> Constants.SECOND_REVIEW_INTERVAL_DAYS // Second review: 6 days
            else -> {
                // Third+ review: multiply by ease factor
                val newInterval = (currentInterval * newEF).toInt()
                // Ensure minimum interval of 1 day
                if (newInterval < 1) 1 else newInterval
            }
        }
    }

    /**
     * Internal method to calculate the next review date timestamp
     * 
     * @param currentTime Current timestamp in milliseconds
     * @param intervalDays Interval in days
     * @return Timestamp for next review date
     */
    private fun calculateNextReviewDateInternal(currentTime: Long, intervalDays: Int): Long {
        val daysInMillis = TimeUnit.DAYS.toMillis(intervalDays.toLong())
        return currentTime + daysInMillis
    }
}


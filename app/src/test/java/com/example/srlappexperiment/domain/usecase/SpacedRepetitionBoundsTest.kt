package com.example.srlappexperiment.domain.usecase

import com.example.srlappexperiment.data.local.database.dao.VocabularyCardDao
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import com.example.srlappexperiment.util.Constants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

/**
 * Bounds and progression tests for the SM-2 engine (complements SpacedRepetitionEngineTest).
 * Verifies floors/ceilings and the 1 -> 6 -> interval*EF progression.
 */
class SpacedRepetitionBoundsTest {

    private lateinit var engine: SpacedRepetitionEngine

    @Before
    fun setup() {
        engine = SpacedRepetitionEngine(mock())
    }

    @Test
    fun `ease factor never drops below minimum after repeated failures`() {
        // Arrange: a fragile card at the floor
        val card = createTestCard(easeFactor = Constants.MIN_EASINESS_FACTOR, interval = 10, timesReviewed = 8)

        // Act: repeated quality-0 reviews
        val relaxed = engine.reviewCard(card, quality = 0)

        // Assert: floor holds and interval resets
        assertEquals(Constants.MIN_EASINESS_FACTOR, relaxed.easeFactor, 0.0001f)
        assertEquals(Constants.FIRST_REVIEW_INTERVAL_DAYS, relaxed.interval)
    }

    @Test
    fun `quality 4 grows interval 1 then 6 then by ease factor`() {
        // Arrange: fresh card
        val fresh = createTestCard(easeFactor = 2.5f, interval = 1, timesReviewed = 0)

        // Act / Assert: first review -> 1 day
        val first = engine.reviewCard(fresh, quality = 4)
        assertEquals(Constants.FIRST_REVIEW_INTERVAL_DAYS, first.interval)

        // Act / Assert: second review -> 6 days
        val second = engine.reviewCard(first, quality = 4)
        assertEquals(Constants.SECOND_REVIEW_INTERVAL_DAYS, second.interval)

        // Act: third review multiplies by the *new* ease factor (rounded)
        val third = engine.reviewCard(second, quality = 4)
        assertTrue("third interval should exceed 6 days", third.interval > 6)
    }

    @Test
    fun `review marks card unsynced and touches timestamps`() {
        // Arrange
        val card = createTestCard(timesReviewed = 2)
        val before = card.updatedAt

        // Act
        val result = engine.reviewCard(card, quality = 5)

        // Assert: synced flag cleared for Firestore sync, counters advanced
        assertEquals(false, result.synced)
        assertTrue(result.updatedAt >= before)
        assertEquals(3, result.timesReviewed)
        assertEquals(1, result.timesCorrect)
    }

    private fun createTestCard(
        easeFactor: Float = Constants.INITIAL_EASINESS_FACTOR,
        interval: Int = 1,
        timesReviewed: Int = 0
    ): VocabularyCard = VocabularyCard(
        id = "bounds-card",
        word = "test",
        translation = "test",
        definition = "test",
        exampleSentence = "test",
        pronunciation = "test",
        difficulty = "intermediate",
        category = "ielts_academic",
        easeFactor = easeFactor,
        interval = interval,
        timesReviewed = timesReviewed
    )
}

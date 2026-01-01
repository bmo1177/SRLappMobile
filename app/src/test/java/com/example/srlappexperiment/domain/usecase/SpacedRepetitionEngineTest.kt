package com.example.srlappexperiment.domain.usecase

import com.example.srlappexperiment.data.local.database.dao.VocabularyCardDao
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import com.example.srlappexperiment.util.Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.concurrent.TimeUnit

/**
 * Unit tests for SpacedRepetitionEngine SM-2 algorithm implementation
 */
class SpacedRepetitionEngineTest {

    private lateinit var mockDao: VocabularyCardDao
    private lateinit var engine: SpacedRepetitionEngine

    @Before
    fun setup() {
        mockDao = mock()
        engine = SpacedRepetitionEngine(mockDao)
    }

    @Test
    fun `reviewCard with quality 5 increases ease factor`() {
        // Given: A card with initial ease factor
        val card = createTestCard(easeFactor = 2.5f, timesReviewed = 5, interval = 10)

        // When: Reviewing with quality 5 (Easy)
        val result = engine.reviewCard(card, quality = 5)

        // Then: Ease factor should increase
        assertTrue("Ease factor should increase for quality 5", result.easeFactor > 2.5f)
        assertEquals("Times reviewed should increment", card.timesReviewed + 1, result.timesReviewed)
        assertEquals("Should mark as correct", card.timesCorrect + 1, result.timesCorrect)
        assertEquals("Should not increment incorrect", card.timesIncorrect, result.timesIncorrect)
    }

    @Test
    fun `reviewCard with quality 0 resets interval to 1 day`() {
        // Given: A card with existing interval
        val card = createTestCard(interval = 30, timesReviewed = 10)

        // When: Reviewing with quality 0 (Again - complete failure)
        val result = engine.reviewCard(card, quality = 0)

        // Then: Interval should reset to 1 day
        assertEquals("Interval should reset to 1 day", 1, result.interval)
        assertEquals("Ease factor should decrease", card.easeFactor > result.easeFactor, true)
        assertEquals("Should mark as incorrect", card.timesIncorrect + 1, result.timesIncorrect)
    }

    @Test
    fun `reviewCard with quality 2 resets interval to 1 day`() {
        // Given: A card with existing interval
        val card = createTestCard(interval = 20, timesReviewed = 5)

        // When: Reviewing with quality 2 (Poor)
        val result = engine.reviewCard(card, quality = 2)

        // Then: Interval should reset to 1 day
        assertEquals("Interval should reset to 1 day", 1, result.interval)
        assertEquals("Should mark as incorrect", card.timesIncorrect + 1, result.timesIncorrect)
    }

    @Test
    fun `reviewCard with quality 3 resets interval to 1 day but continues progression`() {
        // Given: A card with existing interval
        val card = createTestCard(interval = 15, timesReviewed = 3)

        // When: Reviewing with quality 3 (Hard)
        val result = engine.reviewCard(card, quality = 3)

        // Then: Interval should reset to 1 day but continue progression
        assertEquals("Interval should reset to 1 day", 1, result.interval)
        assertEquals("Should mark as correct", card.timesCorrect + 1, result.timesCorrect)
        assertEquals("Times reviewed should increment", card.timesReviewed + 1, result.timesReviewed)
    }

    @Test
    fun `first review sets interval to 1 day`() {
        // Given: A new card (never reviewed)
        val card = createTestCard(timesReviewed = 0, interval = 1)

        // When: First review with quality 4
        val result = engine.reviewCard(card, quality = 4)

        // Then: Interval should be 1 day
        assertEquals("First review should set interval to 1 day", 1, result.interval)
        assertEquals("Times reviewed should be 1", 1, result.timesReviewed)
    }

    @Test
    fun `second review sets interval to 6 days`() {
        // Given: A card after first review
        val card = createTestCard(timesReviewed = 1, interval = 1)

        // When: Second review with quality 4
        val result = engine.reviewCard(card, quality = 4)

        // Then: Interval should be 6 days
        assertEquals("Second review should set interval to 6 days", 6, result.interval)
        assertEquals("Times reviewed should be 2", 2, result.timesReviewed)
    }

    @Test
    fun `third review multiplies interval by ease factor`() {
        // Given: A card after second review with interval 6 and EF 2.5
        val card = createTestCard(timesReviewed = 2, interval = 6, easeFactor = 2.5f)

        // When: Third review with quality 4
        val result = engine.reviewCard(card, quality = 4)

        // Then: Interval should be approximately 6 * 2.5 = 15 days
        val expectedInterval = (6 * 2.5f).toInt()
        assertEquals("Third review should multiply interval by EF", expectedInterval, result.interval)
    }

    @Test
    fun `ease factor never goes below minimum`() {
        // Given: A card with minimum ease factor
        val card = createTestCard(easeFactor = Constants.MIN_EASINESS_FACTOR, timesReviewed = 5)

        // When: Reviewing with quality 0 multiple times (worst case)
        var result = engine.reviewCard(card, quality = 0)
        repeat(10) {
            result = engine.reviewCard(result, quality = 0)
        }

        // Then: Ease factor should never go below minimum
        assertTrue(
            "Ease factor should never go below ${Constants.MIN_EASINESS_FACTOR}",
            result.easeFactor >= Constants.MIN_EASINESS_FACTOR
        )
    }

    @Test
    fun `reviewCard updates lastReviewDate and nextReviewDate`() {
        // Given: A card
        val card = createTestCard()

        // When: Reviewing the card
        val beforeTime = System.currentTimeMillis()
        val result = engine.reviewCard(card, quality = 4)
        val afterTime = System.currentTimeMillis()

        // Then: Dates should be updated
        assertTrue("Last review date should be set", result.lastReviewDate != null)
        assertTrue(
            "Last review date should be recent",
            result.lastReviewDate!! >= beforeTime && result.lastReviewDate!! <= afterTime
        )
        assertTrue("Next review date should be in future", result.nextReviewDate > beforeTime)
    }

    @Test
    fun `reviewCard marks card as unsynced`() {
        // Given: A synced card
        val card = createTestCard(synced = true)

        // When: Reviewing the card
        val result = engine.reviewCard(card, quality = 4)

        // Then: Card should be marked as unsynced
        assertEquals("Card should be marked as unsynced", false, result.synced)
    }

    @Test
    fun `calculateNextReviewDate returns correct timestamp`() {
        // Given: Quality, EF, and interval
        val quality = 4
        val currentEF = 2.5f
        val currentInterval = 10

        // When: Calculating next review date
        val nextDate = engine.calculateNextReviewDate(quality, currentEF, currentInterval)

        // Then: Should be approximately current time + interval days
        val now = System.currentTimeMillis()
        val expectedDays = 25 // 10 days * 2.5 EF
        val expectedMillis = TimeUnit.DAYS.toMillis(expectedDays.toLong())
        val tolerance = TimeUnit.HOURS.toMillis(1) // Allow 1 hour tolerance

        assertTrue(
            "Next review date should be approximately $expectedDays days from now",
            nextDate >= now + expectedMillis - tolerance &&
                    nextDate <= now + expectedMillis + tolerance
        )
    }

    @Test
    fun `getDueCards returns cards from dao`() = runTest {
        // Given: Mock DAO returning cards
        val mockCards = listOf(
            createTestCard(id = "1"),
            createTestCard(id = "2")
        )
        whenever(mockDao.getDueCards(any(), any())).thenReturn(flowOf(mockCards))

        // When: Getting due cards
        val result = engine.getDueCards(limit = 30)

        // Then: Should return cards from DAO
        val cards = result.first()
        assertEquals("Should return cards from DAO", mockCards.size, cards.size)
    }

    @Test
    fun `getNewCards returns new cards from dao`() = runTest {
        // Given: Mock DAO returning new cards
        val mockCards = listOf(createTestCard(id = "new1"))
        whenever(mockDao.getNewCards(any())).thenReturn(flowOf(mockCards))

        // When: Getting new cards
        val result = engine.getNewCards(limit = 10)

        // Then: Should return new cards from DAO
        val cards = result.first()
        assertEquals("Should return new cards from DAO", mockCards.size, cards.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `reviewCard throws exception for invalid quality below range`() {
        val card = createTestCard()
        engine.reviewCard(card, quality = -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `reviewCard throws exception for invalid quality above range`() {
        val card = createTestCard()
        engine.reviewCard(card, quality = 6)
    }

    // Helper function to create test cards
    private fun createTestCard(
        id: String = "test-card-1",
        easeFactor: Float = Constants.INITIAL_EASINESS_FACTOR,
        interval: Int = 1,
        timesReviewed: Int = 0,
        synced: Boolean = false
    ): VocabularyCard {
        return VocabularyCard(
            id = id,
            word = "test",
            translation = "test translation",
            definition = "test definition",
            exampleSentence = "test example",
            pronunciation = "test",
            difficulty = "intermediate",
            category = "ielts_academic",
            easeFactor = easeFactor,
            interval = interval,
            nextReviewDate = System.currentTimeMillis(),
            lastReviewDate = null,
            timesReviewed = timesReviewed,
            timesCorrect = 0,
            timesIncorrect = 0,
            synced = synced,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
}


package com.example.srlappexperiment.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Contract tests for tuning constants. If these fail, the learning
 * engine, database, and notification defaults have drifted.
 */
class ConstantsContractTest {

    @Test
    fun `sm2 bounds are sane`() {
        // Arrange / Act / Assert per coding-standards (AAA)
        assertTrue(Constants.MIN_EASINESS_FACTOR < Constants.INITIAL_EASINESS_FACTOR)
        assertEquals(1, Constants.FIRST_REVIEW_INTERVAL_DAYS)
        assertEquals(6, Constants.SECOND_REVIEW_INTERVAL_DAYS)
        assertEquals(0, Constants.MIN_QUALITY_RATING)
        assertEquals(5, Constants.MAX_QUALITY_RATING)
        assertTrue(Constants.MIN_QUALITY_TO_CONTINUE in 1..5)
    }

    @Test
    fun `database name and version are stable`() {
        assertEquals("srl_app_database", Constants.DATABASE_NAME)
        assertTrue("database version must be >= 3 (indices added in v4)", Constants.DATABASE_VERSION >= 4)
    }

    @Test
    fun `study defaults stay within documented ranges`() {
        assertTrue(Constants.DEFAULT_DUE_CARDS_LIMIT in 1..100)
        assertTrue(Constants.DEFAULT_NEW_CARDS_LIMIT in 1..50)
        assertTrue(Constants.MIN_STUDY_TIME_MINUTES <= Constants.DEFAULT_STUDY_TIME_MINUTES)
        assertTrue(Constants.DEFAULT_STUDY_TIME_MINUTES <= Constants.MAX_STUDY_TIME_MINUTES)
    }
}

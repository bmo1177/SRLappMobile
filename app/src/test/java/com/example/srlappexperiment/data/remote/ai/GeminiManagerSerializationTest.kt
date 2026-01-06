package com.example.srlappexperiment.data.remote.ai

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class GeminiManagerSerializationTest {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Test
    fun `parse VocabularyExplanation from valid JSON`() {
        // Given
        val jsonString = """
            {
              "mnemonic": "Test mnemonic",
              "contextSentence": "This is a test sentence.",
              "usageTips": "Use carefully.",
              "synonyms": ["one", "two"]
            }
        """.trimIndent()

        // When
        val result = jsonParser.decodeFromString<VocabularyExplanation>(jsonString)

        // Then
        assertEquals("Test mnemonic", result.mnemonic)
        assertEquals("This is a test sentence.", result.contextSentence)
        assertEquals("Use carefully.", result.usageTips)
        assertEquals(2, result.synonyms.size)
        assertEquals("one", result.synonyms[0])
    }

    @Test
    fun `parse VocabularyExplanation with extra fields (ignoreUnknownKeys)`() {
        // Given
        val jsonString = """
            {
              "mnemonic": "Test",
              "contextSentence": "Test",
              "usageTips": "Test",
              "synonyms": [],
              "extraField": "Should be ignored"
            }
        """.trimIndent()

        // When
        val result = jsonParser.decodeFromString<VocabularyExplanation>(jsonString)

        // Then
        assertEquals("Test", result.mnemonic)
    }
}

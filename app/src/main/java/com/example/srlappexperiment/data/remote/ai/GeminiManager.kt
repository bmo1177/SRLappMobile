package com.example.srlappexperiment.data.remote.ai

import com.example.srlappexperiment.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for Google Gemini AI integration
 * Provides AI-powered features for vocabulary learning
 */
@Singleton
class GeminiManager @Inject constructor() {

    private val model: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = getApiKey(),
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 1024
            }
        )
    }

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /**
     * Generate an AI-powered explanation for a vocabulary word
     * @param word The vocabulary word
     * @param translation The translation
     * @param difficulty User's proficiency level (beginner, intermediate, advanced)
     * @return Explanation with mnemonic, example sentence, and tips
     */
    suspend fun generateVocabularyExplanation(
        word: String,
        translation: String,
        difficulty: String = "intermediate"
    ): VocabularyExplanation = withContext(Dispatchers.IO) {
        val prompt = """
            You are a language learning assistant helping students prepare for IELTS/TOEFL exams.
            
            Word: "$word"
            Translation: "$translation"
            User Level: $difficulty
            
            Provide a structured explanation in the following JSON format:
            {
              "mnemonic": "A creative memory aid to help remember this word (max 50 words)",
              "contextSentence": "An example sentence using this word in context (relevant to IELTS/TOEFL topics)",
              "usageTips": "Brief tips on when and how to use this word (max 40 words)",
              "synonyms": ["synonym1", "synonym2", "synonym3"]
            }
            
            Important: Return ONLY valid JSON, no markdown formatting or extra text.
        """.trimIndent()

        try {
            val response = model.generateContent(prompt)
            parseJson<VocabularyExplanation>(response)
        } catch (e: Exception) {
            // Fallback to basic explanation if API fails
            VocabularyExplanation(
                mnemonic = "Think of '$word' as '$translation'",
                contextSentence = "The word '$word' means $translation.",
                usageTips = "Use this word in formal writing and speaking.",
                synonyms = emptyList()
            )
        }
    }

    /**
     * Generate dynamic quiz distractors (wrong answers) for gamification
     * @param correctWord The correct word
     * @param correctTranslation The correct translation
     * @param existingWords List of existing words to avoid duplicates
     * @return List of 3 semantically plausible but incorrect options
     */
    suspend fun generateQuizDistractors(
        correctWord: String,
        correctTranslation: String,
        existingWords: List<String>
    ): List<String> = withContext(Dispatchers.IO) {
        val prompt = """
            Generate 3 plausible but INCORRECT translations for the word "$correctWord".
            The correct translation is "$correctTranslation".
            
            Requirements:
            - Each distractor should be semantically similar but clearly wrong
            - Make them challenging enough to test real understanding
            - Avoid these existing words: ${existingWords.joinToString(", ")}
            
            Return ONLY a JSON array of 3 strings, example: ["distractor1", "distractor2", "distractor3"]
            No markdown formatting or extra text.
        """.trimIndent()

        try {
            val response = model.generateContent(prompt)
            parseJson<List<String>>(response)
        } catch (e: Exception) {
            // Fallback to generic distractors
            listOf("Option A", "Option B", "Option C")
        }
    }

    /**
     * Generate a personalized motivational notification message
     * @param userName User's name
     * @param dueCardsCount Number of cards due for review
     * @param streakDays Current learning streak in days
     * @param engagementLevel User's engagement (LOW, MEDIUM, HIGH)
     * @return Personalized notification message
     */
    suspend fun generateMotivationalNotification(
        userName: String,
        dueCardsCount: Int,
        streakDays: Int,
        engagementLevel: String
    ): String = withContext(Dispatchers.IO) {
        val prompt = """
            Generate a short, motivational push notification message (max 60 characters) for a language learner.
            
            Context:
            - User: $userName
            - Cards due: $dueCardsCount
            - Streak: $streakDays days
            - Engagement: $engagementLevel
            
            The message should:
            - Be encouraging and personal
            - Mention the due cards or streak if relevant
            - Be action-oriented (e.g., "Time to practice!")
            
            Return ONLY the message text, no quotes or extra formatting.
        """.trimIndent()

        try {
            val response = model.generateContent(prompt)
            response.text?.trim()?.take(60) ?: "Time to practice your vocabulary! 📚"
        } catch (e: Exception) {
            "You have $dueCardsCount words to review! 🎯"
        }
    }

    /**
     * Get API key from BuildConfig or local.properties
     * In production, this should be fetched securely
     */
    private fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    /**
     * Parse the AI response as a generic JSON type.
     * Handles cleaning of markdown code blocks and finding JSON content.
     */
    private inline fun <reified T> parseJson(response: GenerateContentResponse): T {
        val text = response.text ?: throw Exception("Empty response")
        
        // Extract JSON using Regex
        val jsonContent = extractJsonContent(text)
            
        return jsonParser.decodeFromString(jsonContent)
    }

    /**
     * Extracts JSON content from a string, handling markdown code blocks and raw JSON.
     */
    private fun extractJsonContent(text: String): String {
        // Try to find markdown code block first
        val jsonBlockRegex = Regex("```(?:json)?\\s*([\\s\\S]*?)\\s*```")
        val matchResult = jsonBlockRegex.find(text)
        
        if (matchResult != null) {
            return matchResult.groupValues[1].trim()
        }

        // If no code block, try to find the first '{' or '[' and the last '}' or ']'
        val firstBrace = text.indexOfFirst { it == '{' || it == '[' }
        val lastBrace = text.indexOfLast { it == '}' || it == ']' }

        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return text.substring(firstBrace, lastBrace + 1)
        }

        // Return original text if no pattern matches (might fail parsing, but worth a try if clean)
        return text.trim()
    }
}

/**
 * Data class for AI-generated vocabulary explanation
 */
@Serializable
data class VocabularyExplanation(
    val mnemonic: String,
    val contextSentence: String,
    val usageTips: String,
    val synonyms: List<String>
)

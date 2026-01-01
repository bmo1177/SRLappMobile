package com.example.srlappexperiment.domain.model

/**
 * Status of a learning session in the roadmap
 */
enum class SessionStatus {
    LOCKED,      // Not yet available
    AVAILABLE,   // Unlocked but not started
    CURRENT,     // Currently active session
    COMPLETED    // Finished
}

/**
 * Type of learning session
 */
enum class RoadmapSessionType {
    VOCABULARY,
    GRAMMAR,
    LISTENING,
    QUIZ,
    REVIEW,
    MILESTONE
}

/**
 * A single learning session in the roadmap
 */
data class LearningSession(
    val id: Int,
    val title: String,
    val type: RoadmapSessionType,
    val xpReward: Int,
    val status: SessionStatus,
    val weekNumber: Int,
    val sessionInWeek: Int, // 1 or 2 (2 sessions per week)
    val description: String = ""
)

/**
 * A week in the learning roadmap
 */
data class RoadmapWeek(
    val weekNumber: Int,
    val title: String,
    val theme: String,
    val sessions: List<LearningSession>,
    val isUnlocked: Boolean,
    val progressPercent: Float = 0f
)

/**
 * Complete learning roadmap with 20 weeks
 */
data class LearningRoadmap(
    val weeks: List<RoadmapWeek>,
    val totalXp: Int,
    val currentWeek: Int,
    val currentSession: Int,
    val completedSessions: Int
) {
    val totalSessions: Int get() = weeks.sumOf { it.sessions.size }
    val progressPercent: Float get() = completedSessions.toFloat() / totalSessions
}

/**
 * Factory to create the default 20-week roadmap
 */
object RoadmapFactory {
    
    private val weekThemes = listOf(
        // Weeks 1-4: Basic Vocabulary
        "Getting Started" to "Essential greetings and introductions",
        "Daily Life" to "Common words for everyday situations",
        "Numbers & Time" to "Counting and telling time",
        "Colors & Shapes" to "Visual vocabulary building",
        
        // Weeks 5-8: Essential Grammar
        "Simple Sentences" to "Subject-verb-object patterns",
        "Questions & Answers" to "Forming questions correctly",
        "Past Tense" to "Talking about the past",
        "Future Tense" to "Expressing plans and predictions",
        
        // Weeks 9-12: Listening Practice
        "Conversations" to "Understanding dialogues",
        "Announcements" to "Public speaking comprehension",
        "Interviews" to "Professional listening skills",
        "Accents" to "Understanding different speakers",
        
        // Weeks 13-16: Advanced Vocabulary
        "Academic Words" to "University-level vocabulary",
        "Business English" to "Professional terminology",
        "Idioms & Phrases" to "Native speaker expressions",
        "Collocations" to "Word combinations that sound natural",
        
        // Weeks 17-20: IELTS/TOEFL Prep
        "Reading Strategies" to "Test-taking techniques",
        "Writing Skills" to "Essay and report writing",
        "Speaking Practice" to "Fluency and pronunciation",
        "Final Review" to "Comprehensive exam preparation"
    )
    
    private val sessionTypes = listOf(
        RoadmapSessionType.VOCABULARY,
        RoadmapSessionType.QUIZ,
        RoadmapSessionType.GRAMMAR,
        RoadmapSessionType.LISTENING,
        RoadmapSessionType.VOCABULARY,
        RoadmapSessionType.REVIEW
    )
    
    fun createDefaultRoadmap(completedSessionIds: Set<Int> = emptySet()): LearningRoadmap {
        var sessionId = 1
        var currentFound = false
        
        val weeks = weekThemes.mapIndexed { weekIndex, (title, theme) ->
            val weekNumber = weekIndex + 1
            val isUnlocked = weekNumber == 1 || 
                completedSessionIds.containsAll((1 until sessionId).toList())
            
            val sessions = (1..2).map { sessionInWeek ->
                val id = sessionId++
                val isCompleted = id in completedSessionIds
                val isAvailable = isUnlocked && !isCompleted
                val isCurrent = isAvailable && !currentFound
                
                if (isCurrent) currentFound = true
                
                val status = when {
                    isCompleted -> SessionStatus.COMPLETED
                    isCurrent -> SessionStatus.CURRENT
                    isAvailable -> SessionStatus.AVAILABLE
                    else -> SessionStatus.LOCKED
                }
                
                val type = sessionTypes[(id - 1) % sessionTypes.size]
                val xp = when (type) {
                    RoadmapSessionType.MILESTONE -> 100
                    RoadmapSessionType.REVIEW -> 30
                    RoadmapSessionType.QUIZ -> 50
                    else -> 25
                }
                
                LearningSession(
                    id = id,
                    title = getSessionTitle(weekNumber, sessionInWeek, type),
                    type = type,
                    xpReward = xp,
                    status = status,
                    weekNumber = weekNumber,
                    sessionInWeek = sessionInWeek,
                    description = getSessionDescription(type)
                )
            }
            
            val completedInWeek = sessions.count { it.status == SessionStatus.COMPLETED }
            
            RoadmapWeek(
                weekNumber = weekNumber,
                title = title,
                theme = theme,
                sessions = sessions,
                isUnlocked = isUnlocked,
                progressPercent = completedInWeek.toFloat() / sessions.size
            )
        }
        
        val currentSession = weeks.flatMap { it.sessions }
            .firstOrNull { it.status == SessionStatus.CURRENT }
        
        return LearningRoadmap(
            weeks = weeks,
            totalXp = completedSessionIds.size * 30, // Average XP
            currentWeek = currentSession?.weekNumber ?: 1,
            currentSession = currentSession?.id ?: 1,
            completedSessions = completedSessionIds.size
        )
    }
    
    private fun getSessionTitle(week: Int, session: Int, type: RoadmapSessionType): String {
        val prefix = when (type) {
            RoadmapSessionType.VOCABULARY -> "Vocab"
            RoadmapSessionType.GRAMMAR -> "Grammar"
            RoadmapSessionType.LISTENING -> "Listen"
            RoadmapSessionType.QUIZ -> "Quiz"
            RoadmapSessionType.REVIEW -> "Review"
            RoadmapSessionType.MILESTONE -> "Milestone"
        }
        return "$prefix ${week}.${session}"
    }
    
    private fun getSessionDescription(type: RoadmapSessionType): String = when (type) {
        RoadmapSessionType.VOCABULARY -> "Learn new words and phrases"
        RoadmapSessionType.GRAMMAR -> "Master grammar rules"
        RoadmapSessionType.LISTENING -> "Improve listening comprehension"
        RoadmapSessionType.QUIZ -> "Test your knowledge"
        RoadmapSessionType.REVIEW -> "Reinforce what you've learned"
        RoadmapSessionType.MILESTONE -> "Complete the unit challenge"
    }
}

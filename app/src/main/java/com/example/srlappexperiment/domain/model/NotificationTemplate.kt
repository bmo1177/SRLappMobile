package com.example.srlappexperiment.domain.model

/**
 * Domain model for notification templates with performance metrics
 */
data class NotificationTemplate(
    val id: String = "",
    val text: String = "",
    val segment: EngagementLevel = EngagementLevel.LOW,
    val clickThroughRates: Map<EngagementLevel, Float> = emptyMap(),
    val lastUsedTimestamp: Long? = null
) {
    /**
     * Get CTR for a specific engagement level
     */
    fun getCtrForSegment(segment: EngagementLevel): Float {
        return clickThroughRates[segment] ?: 0f
    }
}

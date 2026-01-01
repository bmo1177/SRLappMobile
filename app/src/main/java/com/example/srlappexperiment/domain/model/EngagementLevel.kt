package com.example.srlappexperiment.domain.model

/**
 * User engagement segments based on activity frequency
 */
enum class EngagementLevel {
    HIGH,   // 5-7 days active in last 7 days
    MEDIUM, // 2-4 days active in last 7 days
    LOW;    // 0-1 days active in last 7 days

    companion object {
        fun fromString(value: String): EngagementLevel {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: LOW
        }
    }
}

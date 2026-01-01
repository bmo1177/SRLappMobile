package com.example.srlappexperiment.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Notification template entity for AI-powered notification system
 */
@Entity(tableName = "notification_templates")
data class NotificationTemplate(
    @PrimaryKey
    val id: String = "",
    val text: String = "",
    val segment: String = "", // high/medium/low
    val clickThroughRate: Float = 0f,
    val lastUsedDate: Long? = null
)


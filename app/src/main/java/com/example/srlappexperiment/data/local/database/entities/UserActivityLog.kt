package com.example.srlappexperiment.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_activity_logs")
data class UserActivityLog(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val activityType: String = "", // login, lesson_complete, purchase, suspend
    val activityData: String = "", // JSON string for flexible data
    val ipAddress: String? = null,
    val deviceInfo: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

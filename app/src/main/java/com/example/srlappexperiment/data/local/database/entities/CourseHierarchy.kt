package com.example.srlappexperiment.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey
    val id: String = "",
    val languageCode: String = "", // en, es
    val targetLanguageCode: String = "", // es
    val title: String = "",
    val description: String = "",
    val level: String = "", // A1, A2, B1, B2
    val totalLessons: Int = 0,
    val isPublished: Boolean = false,
    val iconUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sections")
data class Section(
    @PrimaryKey
    val id: String = "",
    val courseId: String = "",
    val title: String = "",
    val order: Int = 0
)

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey
    val id: String = "",
    val sectionId: String = "",
    val title: String = "",
    val description: String = "",
    val order: Int = 0,
    val estimatedMinutes: Int = 15,
    val isPremiumOnly: Boolean = false,
    val isPublished: Boolean = false
)

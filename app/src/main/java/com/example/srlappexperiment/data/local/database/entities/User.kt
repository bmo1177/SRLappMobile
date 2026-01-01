package com.example.srlappexperiment.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity for local storage
 * Note: This is separate from the domain User model
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String = "",
    val email: String = "",
    val displayName: String? = null,
    val photoURL: String? = null,
    
    // Role & Permissions
    val role: String = "learner", // learner, premium, instructor, admin, support
    val permissions: String = "[]", // JSON array of permission codes
    
    // Status & Billing
    val accountStatus: String = "active", // active, suspended, pending
    val subscriptionTier: String = "free", // free, premium
    val subscriptionEndDate: Long? = null,
    
    // Learning Stats
    val streakCount: Int = 0,
    val totalXp: Int = 0,
    val currentLevel: Int = 1,
    val totalTimeMinutes: Int = 0,
    

    // Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val lastActive: Long = System.currentTimeMillis(),
    val loginCount: Int = 0,
    val synced: Boolean = false,
    val adminNotes: String? = null,

    // New Fields
    val engagementLevel: String? = null,
    val userType: String? = null,
    val targetScore: Int? = null,
    val examDate: Long? = null
)


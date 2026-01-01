package com.example.srlappexperiment.domain.repository

import com.example.srlappexperiment.data.local.database.entities.User
import com.example.srlappexperiment.domain.model.EngagementLevel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user profile and activity tracking
 */
interface UserRepository {
    fun getUser(userId: String): Flow<User?>
    suspend fun saveUser(user: User)
    suspend fun updateEngagementLevel(userId: String, level: EngagementLevel)
    suspend fun updateLastActive(userId: String)
    suspend fun updateProfile(userId: String, displayName: String?, userType: String, targetScore: Int?, examDate: Long?)
    suspend fun deleteAccount(userId: String)
}

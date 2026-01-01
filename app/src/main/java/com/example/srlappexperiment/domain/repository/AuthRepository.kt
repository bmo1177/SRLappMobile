package com.example.srlappexperiment.domain.repository

import com.example.srlappexperiment.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations
 */
interface AuthRepository {
    fun registerWithEmail(email: String, password: String): Flow<Result<User>>
    fun loginWithEmail(email: String, password: String): Flow<Result<User>>
    fun loginWithGoogle(idToken: String): Flow<Result<User>>
    fun logout(): Flow<Result<Unit>>
    fun resetPassword(email: String): Flow<Result<Unit>>
    fun getCurrentUser(): Flow<User?>
    fun updateUserProfile(displayName: String): Flow<Result<Unit>>
    fun saveUserLevel(level: String): Flow<Result<Unit>>
}


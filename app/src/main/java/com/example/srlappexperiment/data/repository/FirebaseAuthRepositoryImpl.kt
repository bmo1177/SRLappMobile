package com.example.srlappexperiment.data.repository

import com.example.srlappexperiment.data.remote.firebase.FirebaseAuthManager
import com.example.srlappexperiment.domain.model.User
import com.example.srlappexperiment.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Firebase implementation of AuthRepository
 */
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val authManager: FirebaseAuthManager
) : AuthRepository {

    override fun registerWithEmail(email: String, password: String): Flow<Result<User>> {
        return authManager.registerWithEmail(email, password)
    }

    override fun loginWithEmail(email: String, password: String): Flow<Result<User>> {
        return authManager.loginWithEmail(email, password)
    }

    override fun loginWithGoogle(idToken: String): Flow<Result<User>> {
        return authManager.loginWithGoogle(idToken)
    }

    override fun logout(): Flow<Result<Unit>> {
        return authManager.logout()
    }

    override fun resetPassword(email: String): Flow<Result<Unit>> {
        return authManager.resetPassword(email)
    }

    override fun getCurrentUser(): Flow<User?> {
        return authManager.getCurrentUser()
    }

    override fun updateUserProfile(displayName: String): Flow<Result<Unit>> {
        return authManager.updateUserProfile(displayName)
    }

    override fun saveUserLevel(level: String): Flow<Result<Unit>> {
        return authManager.saveUserLevel(level)
    }
}


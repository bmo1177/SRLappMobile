package com.example.srlappexperiment.domain.model

import com.example.srlappexperiment.data.local.database.entities.VocabularyCard

/**
 * Sealed class representing the current authentication state of the application
 */
sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

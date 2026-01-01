package com.example.srlappexperiment.util.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*

/**
 * Utility class to handle Firebase Auth errors and map them to user-friendly messages
 */
object AuthErrorUtils {

    fun getErrorMessage(exception: Throwable?): String {
        if (exception == null) return "An unknown error occurred"

        return when (exception) {
            is FirebaseAuthInvalidUserException -> "No account found with this email"
            is FirebaseAuthInvalidCredentialsException -> "Incorrect email or password"
            is FirebaseAuthUserCollisionException -> "An account with this email already exists"
            is FirebaseAuthRecentLoginRequiredException -> "Please log in again to perform this action"
            is FirebaseAuthWeakPasswordException -> "The password is too weak. Use at least 8 characters"
            is FirebaseNetworkException -> "Network error. Please check your internet connection"
            is FirebaseAuthActionCodeException -> "Invalid or expired action code"
            is IllegalArgumentException -> exception.message ?: "Invalid input"
            else -> exception.localizedMessage ?: "An unexpected error occurred. Please try again"
        }
    }
}

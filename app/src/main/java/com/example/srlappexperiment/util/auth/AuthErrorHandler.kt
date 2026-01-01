package com.example.srlappexperiment.util.auth

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException

/**
 * Error handling utilities for authentication
 */
object AuthErrorHandler {

    /**
     * Converts Firebase exceptions to user-friendly error messages
     */
    fun getErrorMessage(exception: Throwable?): String {
        return when (exception) {
            is FirebaseAuthException -> {
                getAuthErrorMessage(exception.errorCode)
            }
            is FirebaseFirestoreException -> {
                getFirestoreErrorMessage(exception.code)
            }
            else -> {
                exception?.message ?: "An unknown error occurred"
            }
        }
    }

    /**
     * Gets user-friendly message from Firebase Auth error code
     */
    private fun getAuthErrorMessage(errorCode: String): String {
        return when (errorCode) {
            "ERROR_WEAK_PASSWORD" -> "Password is too weak. Please use at least 8 characters."
            "ERROR_INVALID_EMAIL" -> "Invalid email address format."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "An account with this email already exists."
            "ERROR_USER_NOT_FOUND" -> "No account found with this email address."
            "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many failed attempts. Please try again later."
            "ERROR_USER_DISABLED" -> "This account has been disabled."
            "ERROR_INVALID_CREDENTIAL" -> "Invalid email or password."
            "ERROR_OPERATION_NOT_ALLOWED" -> "This sign-in method is not enabled."
            "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your connection."
            else -> "Authentication failed: $errorCode"
        }
    }

    /**
     * Gets user-friendly message from Firestore error code
     */
    private fun getFirestoreErrorMessage(code: FirebaseFirestoreException.Code): String {
        return when (code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Permission denied. Please check your account permissions."
            FirebaseFirestoreException.Code.UNAVAILABLE -> "Service temporarily unavailable. Please try again later."
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> "Request timed out. Please check your connection."
            FirebaseFirestoreException.Code.UNAUTHENTICATED -> "Authentication required. Please sign in again."
            else -> "Database error occurred. Please try again."
        }
    }
}


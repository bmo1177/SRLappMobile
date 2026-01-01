package com.example.srlappexperiment.util.auth

import java.util.regex.Pattern

/**
 * Validation utilities for authentication
 */
object AuthValidator {
    
    private const val MIN_PASSWORD_LENGTH = 8
    private val EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"
    )
    
    // Password complexity patterns
    private val HAS_UPPERCASE = Pattern.compile(".*[A-Z].*")
    private val HAS_LOWERCASE = Pattern.compile(".*[a-z].*")
    private val HAS_DIGIT = Pattern.compile(".*\\d.*")

    /**
     * Validates email format
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return EMAIL_PATTERN.matcher(email).matches()
    }

    /**
     * Validates password strength (basic - minimum length only)
     * Requirements: At least 8 characters
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= MIN_PASSWORD_LENGTH
    }

    /**
     * Validates password strength with complexity requirements
     * Requirements:
     * - At least 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     */
    fun isStrongPassword(password: String): Boolean {
        if (password.length < MIN_PASSWORD_LENGTH) return false
        if (!HAS_UPPERCASE.matcher(password).matches()) return false
        if (!HAS_LOWERCASE.matcher(password).matches()) return false
        if (!HAS_DIGIT.matcher(password).matches()) return false
        return true
    }

    /**
     * Gets password validation error message (basic)
     */
    fun getPasswordErrorMessage(password: String): String? {
        return when {
            password.isBlank() -> "Password cannot be empty"
            password.length < MIN_PASSWORD_LENGTH -> "Password must be at least $MIN_PASSWORD_LENGTH characters"
            else -> null
        }
    }

    /**
     * Gets detailed password validation error message with complexity requirements
     */
    fun getStrongPasswordErrorMessage(password: String): String? {
        return when {
            password.isBlank() -> "Password cannot be empty"
            password.length < MIN_PASSWORD_LENGTH -> "Password must be at least $MIN_PASSWORD_LENGTH characters"
            !HAS_UPPERCASE.matcher(password).matches() -> "Password must contain at least one uppercase letter"
            !HAS_LOWERCASE.matcher(password).matches() -> "Password must contain at least one lowercase letter"
            !HAS_DIGIT.matcher(password).matches() -> "Password must contain at least one digit"
            else -> null
        }
    }

    /**
     * Gets email validation error message
     */
    fun getEmailErrorMessage(email: String): String? {
        return when {
            email.isBlank() -> "Email cannot be empty"
            !isValidEmail(email) -> "Please enter a valid email address"
            else -> null
        }
    }

    /**
     * Sanitizes user input by trimming whitespace and removing control characters
     */
    fun sanitizeInput(input: String): String {
        return input.trim().replace(Regex("[\\p{Cntrl}]"), "")
    }

    /**
     * Validates display name
     * Requirements: 2-50 characters, no special characters except spaces
     */
    fun isValidDisplayName(displayName: String): Boolean {
        val trimmed = displayName.trim()
        if (trimmed.length < 2 || trimmed.length > 50) return false
        return trimmed.matches(Regex("^[a-zA-Z0-9 ]+$"))
    }

    /**
     * Gets display name validation error message
     */
    fun getDisplayNameErrorMessage(displayName: String): String? {
        val trimmed = displayName.trim()
        return when {
            trimmed.isBlank() -> "Display name cannot be empty"
            trimmed.length < 2 -> "Display name must be at least 2 characters"
            trimmed.length > 50 -> "Display name cannot exceed 50 characters"
            !trimmed.matches(Regex("^[a-zA-Z0-9 ]+$")) -> "Display name can only contain letters, numbers, and spaces"
            else -> null
        }
    }
}

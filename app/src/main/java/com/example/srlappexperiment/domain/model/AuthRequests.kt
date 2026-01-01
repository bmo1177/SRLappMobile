package com.example.srlappexperiment.domain.model

/**
 * Data classes for authentication requests
 */

data class SignUpRequest(
    val email: String,
    val password: String,
    val displayName: String? = null,
    val userType: String = "general"
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class ResetPasswordRequest(
    val email: String
)

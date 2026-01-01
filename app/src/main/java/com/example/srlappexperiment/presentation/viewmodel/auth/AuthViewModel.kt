package com.example.srlappexperiment.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.srlappexperiment.domain.model.AuthState
import com.example.srlappexperiment.domain.model.User
import com.example.srlappexperiment.domain.repository.AuthRepository
import com.example.srlappexperiment.util.auth.AuthErrorUtils
import com.example.srlappexperiment.util.auth.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for authentication UI
 * Handles session management, rate limiting, and state transitions
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Rate limiting state
    private var failedAttempts = 0
    private var lastAttemptTimestamp = 0L
    private val MAX_ATTEMPTS = 5
    private val RATE_LIMIT_DURATION_MS = 60 * 1000L // 1 minute

    init {
        observeAuthState()
    }

    /**
     * Observes authentication state changes from Firebase
     */
    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getCurrentUser()
                .catch { e ->
                    _authState.value = AuthState.Error(AuthErrorUtils.getErrorMessage(e))
                }
                .collect { user ->
                    _currentUser.value = user
                    _authState.value = if (user != null) {
                        AuthState.Authenticated(user)
                    } else {
                        AuthState.Unauthenticated
                    }
                }
        }
    }

    /**
     * Checks if the user is currently rate-limited
     */
    private fun checkRateLimit(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAttemptTimestamp > RATE_LIMIT_DURATION_MS) {
            // Reset window if more than 1 minute has passed
            failedAttempts = 0
            return false
        }
        
        if (failedAttempts >= MAX_ATTEMPTS) {
            _authState.value = AuthState.Error("Too many failed attempts. Please try again in a minute.")
            return true
        }
        return false
    }

    private fun recordFailedAttempt() {
        failedAttempts++
        lastAttemptTimestamp = System.currentTimeMillis()
    }

    /**
     * Registers a new user with email and password
     */
    fun registerWithEmail(email: String, password: String) {
        if (!AuthValidator.isValidEmail(email)) {
            _authState.value = AuthState.Error("Invalid email format")
            return
        }
        
        if (!AuthValidator.isValidPassword(password)) {
            _authState.value = AuthState.Error("Password must be at least 8 characters")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.registerWithEmail(email, password)
                .catch { e ->
                    _authState.value = AuthState.Error(AuthErrorUtils.getErrorMessage(e))
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { user ->
                            _currentUser.value = user
                            _authState.value = AuthState.Authenticated(user)
                            failedAttempts = 0
                        },
                        onFailure = { e ->
                            _authState.value = AuthState.Error(AuthErrorUtils.getErrorMessage(e))
                        }
                    )
                }
        }
    }

    /**
     * Logs in user with email and password
     */
    fun loginWithEmail(email: String, password: String) {
        if (checkRateLimit()) return

        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.loginWithEmail(email, password)
                .catch { e ->
                    recordFailedAttempt()
                    _authState.value = AuthState.Error(AuthErrorUtils.getErrorMessage(e))
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { user ->
                            _currentUser.value = user
                            _authState.value = AuthState.Authenticated(user)
                            failedAttempts = 0
                        },
                        onFailure = { e ->
                            recordFailedAttempt()
                            _authState.value = AuthState.Error(AuthErrorUtils.getErrorMessage(e))
                        }
                    )
                }
        }
    }

    /**
     * Logs in user with Google Sign-In
     */
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.loginWithGoogle(idToken)
                .catch { e ->
                    _authState.value = AuthState.Error(AuthErrorUtils.getErrorMessage(e))
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { _ ->
                            failedAttempts = 0
                        },
                        onFailure = { e ->
                            _authState.value = AuthState.Error(AuthErrorUtils.getErrorMessage(e))
                        }
                    )
                }
        }
    }

    /**
     * Logs out the current user
     */
    fun logout() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.logout().collect { result ->
                result.fold(
                    onSuccess = {
                        _currentUser.value = null
                        _authState.value = AuthState.Unauthenticated
                    },
                    onFailure = { e ->
                        _authState.value = AuthState.Error(AuthErrorUtils.getErrorMessage(e))
                    }
                )
            }
        }
    }

    /**
     * Sends password reset email
     */
    fun resetPassword(email: String) {
        if (!AuthValidator.isValidEmail(email)) {
            _authState.value = AuthState.Error("Invalid email format")
            return
        }

        viewModelScope.launch {
            authRepository.resetPassword(email).collect { result ->
                result.fold(
                    onSuccess = {
                        // Success could be handled by a separate UI event/state
                    },
                    onFailure = { e ->
                        _authState.value = AuthState.Error(AuthErrorUtils.getErrorMessage(e))
                    }
                )
            }
        }
    }

    /**
     * Updates user profile display name
     */
    fun updateUserProfile(displayName: String) {
        if (displayName.isBlank()) {
            _authState.value = AuthState.Error("Display name cannot be empty")
            return
        }

        viewModelScope.launch {
            authRepository.updateUserProfile(displayName).collect { result ->
                result.fold(
                    onSuccess = {
                        // Profile updated, observeAuthState will fetch updated user
                    },
                    onFailure = { e ->
                        _authState.value = AuthState.Error(AuthErrorUtils.getErrorMessage(e))
                    }
                )
            }
        }
    }

    /**
     * Clears current error state
     */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = if (_currentUser.value != null) {
                AuthState.Authenticated(_currentUser.value!!)
            } else {
                AuthState.Unauthenticated
            }
        }
    }
}


package com.example.srlappexperiment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.srlappexperiment.domain.repository.PreferencesRepository
import com.example.srlappexperiment.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val themeState: StateFlow<ThemeState> = preferencesRepository.userPreferences
        .map { prefs ->
            ThemeState(
                isDarkMode = prefs.isDarkMode,
                useSystemTheme = prefs.useSystemTheme
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeState()
        )

    val isUserLoggedIn: StateFlow<Boolean> = authRepository.getCurrentUser()
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val isOnboardingComplete: StateFlow<Boolean> = preferencesRepository.userPreferences
        .map { it.isOnboarded }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}

data class ThemeState(
    val isDarkMode: Boolean = false,
    val useSystemTheme: Boolean = true
)

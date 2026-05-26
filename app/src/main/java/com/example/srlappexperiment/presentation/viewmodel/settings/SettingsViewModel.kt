package com.example.srlappexperiment.presentation.viewmodel.settings

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.srlappexperiment.domain.model.NotificationSettings
import com.example.srlappexperiment.domain.model.UserPreferences
import com.example.srlappexperiment.domain.repository.AuthRepository
import com.example.srlappexperiment.domain.repository.PreferencesRepository
import com.example.srlappexperiment.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    val notificationSettings: StateFlow<NotificationSettings> = preferencesRepository.notificationSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationSettings())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _effects = MutableSharedFlow<SettingsEffect>()
    val effects: SharedFlow<SettingsEffect> = _effects.asSharedFlow()

    fun updatePreferences(prefs: UserPreferences) {
        viewModelScope.launch {
            try {
                preferencesRepository.updatePreferences(prefs)
                authRepository.getCurrentUser().firstOrNull()?.let { user ->
                    userRepository.updateProfile(
                        user.id,
                        prefs.displayName,
                        prefs.userType,
                        prefs.targetScore,
                        prefs.examDate
                    )
                }
                _effects.emit(SettingsEffect.ShowSnackbar("Preferences updated"))
            } catch (e: Exception) {
                _effects.emit(SettingsEffect.ShowSnackbar(e.message ?: "Failed to update preferences"))
            }
        }
    }

    fun updateNotificationSettings(settings: NotificationSettings) {
        viewModelScope.launch {
            preferencesRepository.updateNotificationSettings(settings)
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val current = userPreferences.value
            preferencesRepository.setDarkMode(!current.isDarkMode)
            preferencesRepository.setUseSystemTheme(false)
        }
    }

    fun setUseSystemTheme(use: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setUseSystemTheme(use)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect()
            _effects.emit(SettingsEffect.NavigateToAuth)
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = authRepository.getCurrentUser().first()?.id
                if (userId != null) {
                    userRepository.deleteAccount(userId)
                    authRepository.logout().collect()
                    preferencesRepository.clearAll()
                    _effects.emit(SettingsEffect.NavigateToAuth)
                }
            } catch (e: Exception) {
                _effects.emit(SettingsEffect.ShowSnackbar(e.message ?: "Failed to delete account"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun exportUserData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val prefs = userPreferences.value
                val notifs = notificationSettings.value

                val data = ExportData(prefs, notifs, System.currentTimeMillis())
                val json = Json.encodeToString(data)

                val file = File(context.cacheDir, "srl_app_data_export.json")
                file.writeText(json)

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                _effects.emit(SettingsEffect.ExportFile(uri))
            } catch (e: Exception) {
                _effects.emit(SettingsEffect.ShowSnackbar("Failed to export data: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }
}

sealed class SettingsEffect {
    data class ShowSnackbar(val message: String) : SettingsEffect()
    data object NavigateToAuth : SettingsEffect()
    data class ExportFile(val uri: Uri) : SettingsEffect()
}

@Serializable
data class ExportData(
    val preferences: UserPreferences,
    val notifications: NotificationSettings,
    val exportDate: Long
)

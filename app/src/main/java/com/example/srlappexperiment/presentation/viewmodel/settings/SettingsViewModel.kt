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

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updatePreferences(prefs: UserPreferences) {
        viewModelScope.launch {
            try {
                preferencesRepository.updatePreferences(prefs)
                // Sync with Firestore if possible
                authRepository.getCurrentUser().firstOrNull()?.let { user ->
                    userRepository.updateProfile(
                        user.id,
                        prefs.displayName,
                        prefs.userType,
                        prefs.targetScore,
                        prefs.examDate
                    )
                }
                _uiState.value = SettingsUiState.Success("Preferences updated")
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "Failed to update preferences")
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
            _uiState.value = SettingsUiState.LoggedOut
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                _uiState.value = SettingsUiState.Loading
                val userId = authRepository.getCurrentUser().first()?.id
                if (userId != null) {
                    userRepository.deleteAccount(userId)
                    authRepository.logout().collect()
                    preferencesRepository.clearAll()
                    _uiState.value = SettingsUiState.LoggedOut
                }
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "Failed to delete account")
            }
        }
    }

    fun exportUserData() {
        viewModelScope.launch {
            try {
                _uiState.value = SettingsUiState.Loading
                val prefs = userPreferences.value
                val notifs = notificationSettings.value
                val exportData = mapOf(
                    "preferences" to prefs,
                    "notifications" to notifs
                    // "export_date" to System.currentTimeMillis() // Removed for simplicity in map or handle differently if needed but standard map with mixed types needs JsonElement or similar.
                    // For now keeping it simple as specific objects, or we can make a wrapper class.
                    // Let's create a wrapper map structure that serialization can handle easily if we want to mix types, 
                    // or just encode the specific objects individually or in a wrapper class.
                    // Ideally we should have an ExportData class.
                    // However, to keep it simple and match previous logic which produced a JSON object:
                )
                
                // Constructing a map of Strings to what? 'Any' is not serializable by default.
                // It's better to define a data class for export.
                
                @Serializable
                data class ExportData(
                    val preferences: UserPreferences,
                    val notifications: NotificationSettings,
                    val exportDate: Long
                )

                val data = ExportData(prefs, notifs, System.currentTimeMillis())
                val json = Json.encodeToString(data)
                
                val file = File(context.cacheDir, "srl_app_data_export.json")
                file.writeText(json)
                
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                _uiState.value = SettingsUiState.ExportSuccess(uri)
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("Failed to export data: ${e.message}")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = SettingsUiState.Idle
    }
}

sealed class SettingsUiState {
    object Idle : SettingsUiState()
    object Loading : SettingsUiState()
    object LoggedOut : SettingsUiState()
    data class Success(val message: String) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
    data class ExportSuccess(val uri: Uri) : SettingsUiState()
}

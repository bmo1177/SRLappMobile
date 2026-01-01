package com.example.srlappexperiment.presentation.viewmodel.placement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.srlappexperiment.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacementViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlacementUiState>(PlacementUiState.Idle)
    val uiState: StateFlow<PlacementUiState> = _uiState.asStateFlow()

    fun saveResult(level: String) {
        viewModelScope.launch {
            _uiState.value = PlacementUiState.Loading
            authRepository.saveUserLevel(level)
                .catch { e ->
                    _uiState.value = PlacementUiState.Error(e.message ?: "Failed to save result")
                }
                .collect { result ->
                    result.fold(
                        onSuccess = {
                            _uiState.value = PlacementUiState.Success
                        },
                        onFailure = { e ->
                            _uiState.value = PlacementUiState.Error(e.message ?: "Failed to save result")
                        }
                    )
                }
        }
    }
}

sealed class PlacementUiState {
    object Idle : PlacementUiState()
    object Loading : PlacementUiState()
    object Success : PlacementUiState()
    data class Error(val message: String) : PlacementUiState()
}

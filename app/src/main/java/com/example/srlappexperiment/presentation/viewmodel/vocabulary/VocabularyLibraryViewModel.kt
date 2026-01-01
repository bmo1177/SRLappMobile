package com.example.srlappexperiment.presentation.viewmodel.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import com.example.srlappexperiment.domain.repository.VocabularyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyLibraryViewModel @Inject constructor(
    private val repository: VocabularyRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow<String?>(null)
    val selectedDifficulty = _selectedDifficulty.asStateFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            // Simulate loading or wait for repository initialization
            delay(800)
            _isLoading.value = false
        }
    }

    val filteredCards: StateFlow<List<VocabularyCard>> = combine(
        repository.getAllCards(),
        _searchQuery,
        _selectedDifficulty
    ) { cards, query, difficulty ->
        cards.filter { card ->
            val matchesQuery = card.word.contains(query, ignoreCase = true) || 
                             card.translation.contains(query, ignoreCase = true)
            val matchesDifficulty = difficulty == null || card.difficulty == difficulty
            matchesQuery && matchesDifficulty
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setDifficulty(difficulty: String?) {
        _selectedDifficulty.value = difficulty
    }
}

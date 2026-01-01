package com.example.srlappexperiment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.srlappexperiment.data.local.database.dao.TeacherDao
import com.example.srlappexperiment.data.local.database.entities.TeacherCompetence
import com.example.srlappexperiment.data.local.database.entities.TeacherDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CurriculumUiState(
    val domains: List<TeacherDomain> = emptyList(),
    val competences: Map<Int, List<TeacherCompetence>> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CurriculumViewModel @Inject constructor(
    private val teacherDao: TeacherDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurriculumUiState())
    val uiState: StateFlow<CurriculumUiState> = _uiState.asStateFlow()

    init {
        loadCurriculum()
    }

    private fun loadCurriculum() {
        viewModelScope.launch {
            teacherDao.getAllDomains()
                .onEach { domains ->
                    val competencesMap = mutableMapOf<Int, List<TeacherCompetence>>()
                    domains.forEach { domain ->
                        // In a real app we'd use a more efficient way to fetch all at once
                        // but for pre-populated data this is fine for now
                        teacherDao.getCompetencesByDomain(domain.domain_id).firstOrNull()?.let {
                            competencesMap[domain.domain_id] = it
                        }
                    }
                    _uiState.update { it.copy(domains = domains, competences = competencesMap, isLoading = false) }
                }
                .collect()
        }
    }
}

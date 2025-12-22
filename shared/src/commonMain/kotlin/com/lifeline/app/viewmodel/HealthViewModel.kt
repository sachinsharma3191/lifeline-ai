package com.lifeline.app.viewmodel

import com.lifeline.app.ai.AiClient
import com.lifeline.app.domain.health.HealthTimelineEntry
import com.lifeline.app.domain.health.Symptom
import com.lifeline.app.repository.HealthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HealthViewModel(
    private val repository: HealthRepository,
    private val aiClient: AiClient,
    private val scope: CoroutineScope
) {
    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()
    
    private val _symptoms = MutableStateFlow<List<Symptom>>(emptyList())
    val symptoms: StateFlow<List<Symptom>> = _symptoms.asStateFlow()
    
    private val _timelineEntries = MutableStateFlow<List<HealthTimelineEntry>>(emptyList())
    val timelineEntries: StateFlow<List<HealthTimelineEntry>> = _timelineEntries.asStateFlow()
    
    init {
        loadSymptoms()
        loadTimelineEntries()
    }
    
    fun addSymptom(symptom: Symptom) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.addSymptom(symptom)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun addTimelineEntry(entry: HealthTimelineEntry) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.addTimelineEntry(entry)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun askAi(prompt: String) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, aiResponse = null, error = null) }
            try {
                val response = aiClient.processRequest(prompt, emptyMap())
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        aiResponse = response.text
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    private fun loadSymptoms() {
        scope.launch {
            repository.getSymptoms(null, null)
                .collect { _symptoms.value = it }
        }
    }
    
    private fun loadTimelineEntries() {
        scope.launch {
            repository.getTimelineEntries(null, null)
                .collect { _timelineEntries.value = it }
        }
    }
}

data class HealthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val aiResponse: String? = null
)

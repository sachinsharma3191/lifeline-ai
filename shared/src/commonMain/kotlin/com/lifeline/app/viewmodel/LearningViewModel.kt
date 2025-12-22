package com.lifeline.app.viewmodel

import com.lifeline.app.ai.AiClient
import com.lifeline.app.domain.learning.LearningGoal
import com.lifeline.app.domain.learning.LearningModule
import com.lifeline.app.repository.LearningRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LearningViewModel(
    private val repository: LearningRepository,
    private val aiClient: AiClient,
    private val scope: CoroutineScope
) {
    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()
    
    private val _goals = MutableStateFlow<List<LearningGoal>>(emptyList())
    val goals: StateFlow<List<LearningGoal>> = _goals.asStateFlow()
    
    private val _modules = MutableStateFlow<List<LearningModule>>(emptyList())
    val modules: StateFlow<List<LearningModule>> = _modules.asStateFlow()
    
    init {
        loadGoals()
        loadModules()
    }
    
    fun addGoal(goal: LearningGoal) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.addGoal(goal)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun completeModule(id: String) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.completeModule(id)
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
                val response = aiClient.processRequest(prompt)
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
    
    private fun loadGoals() {
        repository.getGoals()
            .onEach { _goals.value = it }
            .launchIn(scope)
    }
    
    private fun loadModules() {
        repository.getModules()
            .onEach { _modules.value = it }
            .launchIn(scope)
    }
}

data class LearningUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val aiResponse: String? = null
)

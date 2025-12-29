package com.lifeline.app.viewmodel

import com.lifeline.app.ai.AiClient
import com.lifeline.app.domain.services.CommunityService
import com.lifeline.app.repository.ServicesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ServicesViewModel(
    private val repository: ServicesRepository,
    private val aiClient: AiClient,
    private val scope: CoroutineScope
) {
    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()
    
    private val _services = MutableStateFlow<List<CommunityService>>(emptyList())
    val services: StateFlow<List<CommunityService>> = _services.asStateFlow()
    
    init {
        loadServices()
    }
    
    fun searchServices(query: String) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, searchQuery = query) }
            try {
                repository.searchServices(query)
                    .collect { services ->
                        _services.value = services
                        _uiState.update { it.copy(isLoading = false) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun filterByCategory(category: String?) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, selectedCategory = category) }
            try {
                repository.getServices(category)
                    .collect { services ->
                        _services.value = services
                        _uiState.update { it.copy(isLoading = false) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun addService(service: CommunityService) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.addService(service)
                loadServices()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateService(service: CommunityService) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.updateService(service)
                _services.update { current ->
                    current.map { if (it.id == service.id) service else it }
                }
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
    
    private fun loadServices() {
        scope.launch {
            repository.getServices()
                .collect { services ->
                    _services.value = services
                }
        }
    }
}

data class ServicesUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val aiResponse: String? = null
)

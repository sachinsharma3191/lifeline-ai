package com.lifeline.app.viewmodel

import com.lifeline.app.ai.AiClient
import com.lifeline.app.domain.finance.FinancialGoal
import com.lifeline.app.domain.finance.Transaction
import com.lifeline.app.repository.FinanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MoneyViewModel(
    private val repository: FinanceRepository,
    private val aiClient: AiClient,
    private val scope: CoroutineScope
) {
    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val _goals = MutableStateFlow<List<FinancialGoal>>(emptyList())
    val goals: StateFlow<List<FinancialGoal>> = _goals.asStateFlow()
    
    init {
        loadTransactions()
        loadGoals()
    }
    
    fun addTransaction(transaction: Transaction) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.addTransaction(transaction)
                _transactions.update { current ->
                    (listOf(transaction) + current)
                        .distinctBy { it.id }
                        .sortedByDescending { it.timestamp }
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.updateTransaction(transaction)
                _transactions.update { current ->
                    current
                        .map { if (it.id == transaction.id) transaction else it }
                        .sortedByDescending { it.timestamp }
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun addGoal(goal: FinancialGoal) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.addGoal(goal)
                _goals.update { current ->
                    (listOf(goal) + current)
                        .distinctBy { it.id }
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun updateGoal(goal: FinancialGoal) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.updateGoal(goal)
                _goals.update { current ->
                    current.map { if (it.id == goal.id) goal else it }
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
    
    private fun loadTransactions() {
        scope.launch {
            repository.getTransactions(null, null)
                .collect { _transactions.value = it }
        }
    }
    
    private fun loadGoals() {
        scope.launch {
            repository.getGoals()
                .collect { _goals.value = it }
        }
    }
}

data class FinanceUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val aiResponse: String? = null
)

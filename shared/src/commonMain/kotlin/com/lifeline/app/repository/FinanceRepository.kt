package com.lifeline.app.repository

import com.lifeline.app.domain.finance.FinancialGoal
import com.lifeline.app.domain.finance.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface FinanceRepository {
    suspend fun addTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun getTransactions(startDate: Instant?, endDate: Instant?): Flow<List<Transaction>>
    suspend fun addGoal(goal: FinancialGoal)
    suspend fun getGoals(): Flow<List<FinancialGoal>>
    suspend fun updateGoal(goal: FinancialGoal)
    suspend fun deleteTransaction(id: String)
    suspend fun deleteGoal(id: String)
}


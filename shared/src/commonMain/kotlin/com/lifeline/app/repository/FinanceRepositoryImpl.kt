package com.lifeline.app.repository

import com.lifeline.app.database.LifelineDatabase
import com.lifeline.app.domain.finance.FinancialGoal
import com.lifeline.app.domain.finance.Transaction
import com.lifeline.app.domain.finance.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant

class FinanceRepositoryImpl(
    private val database: LifelineDatabase
) : FinanceRepository {
    
    override suspend fun addTransaction(transaction: Transaction) {
        database.financeQueries.insertTransaction(
            id = transaction.id,
            amount = transaction.amount,
            type = transaction.type.name,
            category = transaction.category,
            timestamp = transaction.timestamp.toEpochMilliseconds(),
            description = transaction.description,
            tags = transaction.tags.joinToString(",")
        )
    }
    
    override suspend fun getTransactions(startDate: Instant?, endDate: Instant?): Flow<List<Transaction>> = flow {
        val startTimestamp = startDate?.toEpochMilliseconds()
        val endTimestamp = endDate?.toEpochMilliseconds()
        
        val rows = database.financeQueries.getAllTransactions(startTimestamp, endTimestamp).executeAsList()
        val transactions = rows.map { row ->
            Transaction(
                id = row.id,
                amount = row.amount,
                type = TransactionType.valueOf(row.type),
                category = row.category,
                timestamp = Instant.fromEpochMilliseconds(row.timestamp),
                description = row.description,
                tags = row.tags?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            )
        }
        emit(transactions)
    }
    
    override suspend fun addGoal(goal: FinancialGoal) {
        database.financeQueries.insertFinancialGoal(
            id = goal.id,
            name = goal.name,
            target_amount = goal.targetAmount,
            current_amount = goal.currentAmount,
            deadline = goal.deadline?.toEpochMilliseconds(),
            category = goal.category
        )
    }
    
    override suspend fun getGoals(): Flow<List<FinancialGoal>> = flow {
        val rows = database.financeQueries.getAllFinancialGoals().executeAsList()
        val goals = rows.map { row ->
            FinancialGoal(
                id = row.id,
                name = row.name,
                targetAmount = row.target_amount,
                currentAmount = row.current_amount,
                deadline = row.deadline?.let { Instant.fromEpochMilliseconds(it) },
                category = row.category
            )
        }
        emit(goals)
    }
    
    override suspend fun updateGoal(goal: FinancialGoal) {
        database.financeQueries.updateFinancialGoal(
            id = goal.id,
            name = goal.name,
            target_amount = goal.targetAmount,
            current_amount = goal.currentAmount,
            deadline = goal.deadline?.toEpochMilliseconds(),
            category = goal.category
        )
    }
    
    override suspend fun deleteTransaction(id: String) {
        database.financeQueries.deleteTransaction(id)
    }
    
    override suspend fun deleteGoal(id: String) {
        database.financeQueries.deleteFinancialGoal(id)
    }
}

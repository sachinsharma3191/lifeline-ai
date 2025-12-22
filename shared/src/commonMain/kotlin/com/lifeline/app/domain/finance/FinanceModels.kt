package com.lifeline.app.domain.finance

import kotlinx.datetime.Instant

data class Transaction(
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val timestamp: Instant,
    val description: String? = null,
    val tags: List<String> = emptyList()
)

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER
}

data class FinancialGoal(
    val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: Instant?,
    val category: String
)


package com.lifeline.app.ai

import com.lifeline.app.domain.finance.FinancialGoal
import com.lifeline.app.domain.finance.Transaction
import com.lifeline.app.domain.finance.TransactionType
import com.lifeline.app.domain.health.Symptom
import com.lifeline.app.domain.learning.GoalStatus
import com.lifeline.app.domain.learning.LearningGoal
import com.lifeline.app.domain.learning.LearningModule
import com.lifeline.app.domain.services.CommunityService
import kotlinx.coroutines.delay

/**
 * Data-driven offline AI coach that analyzes local app data.
 */
class OfflineInsightsAiClient : AiClient {

    override suspend fun processRequest(prompt: String, context: Map<String, Any>): AiResponse {
        delay(50)

        val normalized = prompt.trim().lowercase()
        val text = when {
            normalized.contains("finance") || normalized.contains("money") ||
                normalized.contains("budget") || normalized.contains("expense") ||
                normalized.contains("summary") && context.containsKey("transactions") ->
                financeInsight(normalized, context)

            normalized.contains("health") || normalized.contains("symptom") ||
                normalized.contains("trend") && context.containsKey("symptoms") ->
                healthInsight(context)

            normalized.contains("learn") || normalized.contains("study") ||
                normalized.contains("progress") && context.containsKey("learningGoals") ->
                learningInsight(context)

            normalized.contains("service") || normalized.contains("community") ||
                normalized.contains("help") && context.containsKey("services") ->
                servicesInsight(normalized, context)

            else -> generalInsight(context)
        }

        return AiResponse(
            text = text,
            confidence = 0.85f,
            source = AiSource.OFFLINE_INSIGHTS,
            metadata = mapOf("type" to "offline_insights")
        )
    }

    override suspend fun isAvailable(): Boolean = true

    @Suppress("UNCHECKED_CAST")
    private fun financeInsight(prompt: String, context: Map<String, Any>): String {
        val transactions = context["transactions"] as? List<Transaction> ?: emptyList()
        val goals = context["financialGoals"] as? List<FinancialGoal> ?: emptyList()

        if (transactions.isEmpty()) {
            return "No transactions yet. Start tracking rent, groceries, and transit to see personalized savings tips for students and relocators."
        }

        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val net = income - expenses

        val topCategory = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .maxByOrNull { (_, items) -> items.sumOf { it.amount } }
            ?.key

        val studentCategories = listOf("Rent", "Groceries", "Transit", "Tuition", "Dining")
        val savingsTips = studentCategories
            .filter { cat -> transactions.none { it.category.equals(cat, ignoreCase = true) } }
            .take(2)
            .joinToString(", ")
            .ifBlank { "compare local transit passes and meal plans" }

        return buildString {
            appendLine("Finance snapshot (offline):")
            appendLine("• Income: $${formatAmount(income)} | Expenses: $${formatAmount(expenses)} | Net: $${formatAmount(net)}")
            topCategory?.let { appendLine("• Top expense category: $it") }
            if (prompt.contains("top") || prompt.contains("category")) {
                appendLine("• Focus on $topCategory to improve monthly savings.")
            }
            if (goals.isNotEmpty()) {
                val avgProgress = goals.map { it.currentAmount / it.targetAmount.coerceAtLeast(1.0) }.average()
                appendLine("• Goal progress: ${(avgProgress * 100).toInt()}% average across ${goals.size} goal(s).")
            }
            append("Tip for students & relocators: track $savingsTips to unlock localized cost-saving insights.")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun healthInsight(context: Map<String, Any>): String {
        val symptoms = context["symptoms"] as? List<Symptom> ?: emptyList()
        if (symptoms.isEmpty()) {
            return "No symptoms logged. Track sleep, stress, and fatigue to spot patterns during relocation or exam periods."
        }

        val avgSeverity = symptoms.map { it.severity }.average()
        val topSymptom = symptoms.groupBy { it.name }.maxByOrNull { it.value.size }?.key
        val recentHigh = symptoms.filter { it.severity >= 7 }.take(3)

        return buildString {
            appendLine("Health trends (offline):")
            appendLine("• ${symptoms.size} symptom entries | Average severity: ${"%.1f".format(avgSeverity)}/10")
            topSymptom?.let { appendLine("• Most frequent: $it") }
            if (recentHigh.isNotEmpty()) {
                appendLine("• Recent high-severity: ${recentHigh.joinToString { it.name }}")
            }
            append("Consider logging daily notes during busy weeks to share clearer patterns with campus health resources.")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun learningInsight(context: Map<String, Any>): String {
        val goals = context["learningGoals"] as? List<LearningGoal> ?: emptyList()
        val modules = context["learningModules"] as? List<LearningModule> ?: emptyList()

        val completedModules = modules.count { it.completed }
        val inProgressGoals = goals.count { it.status == GoalStatus.IN_PROGRESS }
        val avgProgress = if (goals.isNotEmpty()) goals.map { it.progress }.average() else 0.0

        return buildString {
            appendLine("Learning progress (offline):")
            appendLine("• ${goals.size} goal(s) | ${completedModules}/${modules.size} modules completed")
            if (goals.isNotEmpty()) {
                appendLine("• Average goal progress: ${(avgProgress * 100).toInt()}%")
            }
            if (inProgressGoals > 0) {
                appendLine("• $inProgressGoals goal(s) in progress — block 25-minute study sessions.")
            }
            append("Try completing one module this week to build momentum before midterms.")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun servicesInsight(prompt: String, context: Map<String, Any>): String {
        val services = context["services"] as? List<CommunityService> ?: emptyList()
        val query = context["searchQuery"] as? String ?: ""

        if (services.isEmpty()) {
            return "Search for Clinic, Food, Legal, or Housing near Westcliff, UCI, or the Bay Area to find localized community resources."
        }

        val categories = services.map { it.category.name.replace('_', ' ') }.distinct().take(4)
        return buildString {
            appendLine("Services help (offline):")
            appendLine("• Found ${services.size} result(s)${if (query.isNotBlank()) " for \"$query\"" else ""}")
            appendLine("• Categories: ${categories.joinToString(", ")}")
            services.firstOrNull()?.location?.let { appendLine("• Nearest match: $it") }
            append("Tap a service address to open Maps and verify hours before visiting.")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun generalInsight(context: Map<String, Any>): String {
        val transactions = context["transactions"] as? List<Transaction>
        val symptoms = context["symptoms"] as? List<Symptom>
        val goals = context["learningGoals"] as? List<LearningGoal>

        val hints = buildList {
            if (transactions != null) add("Finance summary")
            if (symptoms != null) add("Health trends")
            if (goals != null) add("Learning progress")
            add("Services help")
        }

        return "Lifeline AI coach (offline). Built for students & relocators in pilot areas. Try: ${hints.joinToString(", ")}."
    }

    private fun formatAmount(value: Double): String {
        val rounded = (value * 100).toLong() / 100.0
        return if (rounded == rounded.toLong().toDouble()) {
            rounded.toLong().toString()
        } else {
            "%.2f".format(rounded)
        }
    }
}

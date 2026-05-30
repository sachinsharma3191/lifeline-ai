package com.lifeline.app.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifeline.app.config.AppConfigLoader
import com.lifeline.app.config.iconForConfigKey
import com.lifeline.app.config.templateSubtitle
import com.lifeline.app.domain.finance.TransactionType
import com.lifeline.app.navigation.HomeComponent
import com.lifeline.app.utils.formatDouble

@Composable
fun HomeScreen(component: HomeComponent) {
    val config = remember { AppConfigLoader.get() }
    val home = config.screens.home
    val appInfo = config.app

    val transactions by component.moneyViewModel.transactions.collectAsState()
    val goals by component.moneyViewModel.goals.collectAsState()
    val symptoms by component.healthViewModel.symptoms.collectAsState()
    val learningGoals by component.learningViewModel.goals.collectAsState()

    val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val expenses = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val net = income - expenses

    val metricValues = mapOf(
        "income" to income,
        "expenses" to expenses,
        "net" to net
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(config.theme.spacing.screenPadding.dp),
        verticalArrangement = Arrangement.spacedBy(config.theme.spacing.sectionGap.dp)
    ) {
        Text(
            text = appInfo.name,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = appInfo.tagline,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(config.theme.spacing.cardPadding.dp)) {
                Text(
                    text = home.pilotAreasTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(config.theme.spacing.chipGap.dp)
                ) {
                    home.pilotAreas.forEach { area ->
                        AssistChip(
                            onClick = {},
                            label = { Text(area) },
                            leadingIcon = {
                                Icon(
                                    imageVector = iconForConfigKey("services"),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        Text(
            text = home.financialSnapshotTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(config.theme.spacing.chipGap.dp)
        ) {
            home.metrics.forEach { metric ->
                val value = metricValues[metric.id] ?: 0.0
                MetricCard(
                    label = metric.label,
                    value = "$${formatDouble(value)}",
                    modifier = Modifier.weight(1f),
                    highlight = metric.id != "net" || net >= 0
                )
            }
        }

        Text(
            text = home.northStarText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = home.dashboardTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(config.theme.spacing.chipGap.dp)
        ) {
            home.dashboardCards.filter { it.id != "learning" }.forEach { card ->
                DashboardCard(
                    title = card.title,
                    subtitle = templateSubtitle(
                        template = card.subtitleTemplate,
                        transactionCount = transactions.size,
                        goalCount = goals.size,
                        symptomCount = symptoms.size,
                        learningGoalCount = learningGoals.size
                    ),
                    icon = { Icon(iconForConfigKey(card.icon), contentDescription = null) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        home.dashboardCards.firstOrNull { it.id == "learning" }?.let { card ->
            DashboardCard(
                title = card.title,
                subtitle = templateSubtitle(
                    template = card.subtitleTemplate,
                    learningGoalCount = learningGoals.size
                ),
                icon = { Icon(iconForConfigKey(card.icon), contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(config.theme.spacing.cardPadding.dp)) {
                Text(
                    text = home.valuePropositionTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = home.valuePropositionItems.joinToString("\n") { "• $it" },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = true
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (highlight) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            icon()
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

package com.lifeline.app.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifeline.app.domain.finance.TransactionType
import com.lifeline.app.navigation.HomeComponent
import com.lifeline.app.utils.formatDouble

@Composable
fun HomeScreen(component: HomeComponent) {
    val transactions by component.moneyViewModel.transactions.collectAsState()
    val goals by component.moneyViewModel.goals.collectAsState()
    val symptoms by component.healthViewModel.symptoms.collectAsState()
    val learningGoals by component.learningViewModel.goals.collectAsState()

    val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val expenses = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val net = income - expenses

    val pilotAreas = listOf("Westcliff University", "UCI Irvine", "LA / Bay Area")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Lifeline AI",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Your AI-powered lifestyle coach for students and relocators — finance, health, learning, and localized community services in one place.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Pilot launch areas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pilotAreas.forEach { area ->
                        AssistChip(
                            onClick = {},
                            label = { Text(area) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.LocationOn,
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
            text = "Financial wellness snapshot",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                label = "Income",
                value = "$${formatDouble(income)}",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Expenses",
                value = "$${formatDouble(expenses)}",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Net",
                value = "$${formatDouble(net)}",
                modifier = Modifier.weight(1f),
                highlight = net >= 0
            )
        }

        Text(
            text = "North Star: track monthly active use and cost savings as you build healthier money habits.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Your dashboard",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DashboardCard(
                title = "Finance",
                subtitle = "${transactions.size} transactions · ${goals.size} goals",
                icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                modifier = Modifier.weight(1f)
            )
            DashboardCard(
                title = "Health",
                subtitle = "${symptoms.size} symptoms logged",
                icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                modifier = Modifier.weight(1f)
            )
        }

        DashboardCard(
            title = "Learning",
            subtitle = "${learningGoals.size} goals · offline AI study coach",
            icon = { Icon(Icons.Default.School, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Value proposition",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Personalized cost-saving insights from your local data\n" +
                        "• Lifestyle support beyond budgeting (health, learning, services)\n" +
                        "• Offline AI coach — no API keys or internet required\n" +
                        "• Localized community resources for new campuses and cities",
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

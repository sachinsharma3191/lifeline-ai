package com.lifeline.app.config

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector
import com.lifeline.app.navigation.RootComponent

fun tabFromConfigId(id: String): RootComponent.Tab? = when (id.lowercase()) {
    "home" -> RootComponent.Tab.HOME
    "health" -> RootComponent.Tab.HEALTH
    "finance" -> RootComponent.Tab.FINANCE
    "learning" -> RootComponent.Tab.LEARNING
    "services" -> RootComponent.Tab.SERVICES
    else -> null
}

fun iconForConfigKey(key: String): ImageVector = when (key.lowercase()) {
    "home" -> Icons.Default.Home
    "heart", "health" -> Icons.Default.Favorite
    "finance" -> Icons.Default.AccountBalance
    "learning" -> Icons.Default.School
    "services" -> Icons.Default.LocationOn
    else -> Icons.Default.Home
}

fun templateSubtitle(
    template: String,
    transactionCount: Int = 0,
    goalCount: Int = 0,
    symptomCount: Int = 0,
    learningGoalCount: Int = 0
): String = template
    .replace("{transactionCount}", transactionCount.toString())
    .replace("{goalCount}", goalCount.toString())
    .replace("{symptomCount}", symptomCount.toString())
    .replace("{learningGoalCount}", learningGoalCount.toString())

fun mapsUrl(template: String, address: String): String {
    val query = address.replace(" ", "+")
    return template.replace("{query}", query)
}

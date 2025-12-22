package com.lifeline.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.lifeline.app.navigation.RootComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(component: RootComponent) {
    val stack by component.stack.subscribeAsState()
    val activeChild = stack.active.instance
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                RootComponent.Tab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = stack.active.configuration == tab,
                        onClick = { component.onTabSelected(tab) },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    RootComponent.Tab.HOME -> Icons.Default.Home
                                    RootComponent.Tab.HEALTH -> Icons.Default.Favorite
                                    RootComponent.Tab.FINANCE -> Icons.Default.AccountBalance
                                    RootComponent.Tab.LEARNING -> Icons.Default.School
                                    RootComponent.Tab.SERVICES -> Icons.Default.LocationOn
                                },
                                contentDescription = tab.name
                            )
                        },
                        label = { Text(tab.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val child = activeChild) {
                is RootComponent.Child.Home -> HomeScreen(child.component)
                is RootComponent.Child.Health -> HealthScreen(child.component)
                is RootComponent.Child.Finance -> FinanceScreen(child.component)
                is RootComponent.Child.Learning -> LearningScreen(child.component)
                is RootComponent.Child.Services -> ServicesScreen(child.component)
            }
        }
    }
}


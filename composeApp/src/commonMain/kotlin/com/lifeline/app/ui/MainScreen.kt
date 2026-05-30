package com.lifeline.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.lifeline.app.config.AppConfigLoader
import com.lifeline.app.config.iconForConfigKey
import com.lifeline.app.config.tabFromConfigId
import com.lifeline.app.navigation.RootComponent
import com.lifeline.app.utils.subscribeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(component: RootComponent) {
    val config = remember { AppConfigLoader.get() }
    val stackValue by component.stack.subscribeAsState()
    val stack = stackValue as? com.arkivanov.decompose.router.stack.ChildStack<*, RootComponent.Child>
    val activeChild = stack?.active?.instance
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                config.tabs.forEach { tabConfig ->
                    val tab = tabFromConfigId(tabConfig.id) ?: return@forEach
                    NavigationBarItem(
                        selected = (stack?.active?.configuration as? RootComponent.Tab) == tab,
                        onClick = { component.onTabSelected(tab) },
                        icon = {
                            Icon(
                                imageVector = iconForConfigKey(tabConfig.icon),
                                contentDescription = tabConfig.label
                            )
                        },
                        label = { Text(tabConfig.label) }
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
                null -> {}
            }
        }
    }
}

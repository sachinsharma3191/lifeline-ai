package com.lifeline.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.lifeline.app.database.DatabaseDriverFactory
import com.lifeline.app.navigation.RootComponent
import com.lifeline.app.navigation.RootComponentImpl
import com.lifeline.app.ui.MainScreen
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry

@Composable
fun App(
    databaseDriverFactory: DatabaseDriverFactory? = null
) {
    MaterialTheme {
        val lifecycle = remember { LifecycleRegistry() }
        val componentContext = remember {
            DefaultComponentContext(lifecycle = lifecycle)
        }
        
        val appContainer = remember {
            com.lifeline.app.AppContainer(
                databaseDriverFactory = databaseDriverFactory ?: createDatabaseDriverFactory()
            )
        }
        
        val rootComponent = remember {
            RootComponentImpl(componentContext, appContainer)
        }
        
        MainScreen(rootComponent)
    }
}

expect fun createDatabaseDriverFactory(): DatabaseDriverFactory
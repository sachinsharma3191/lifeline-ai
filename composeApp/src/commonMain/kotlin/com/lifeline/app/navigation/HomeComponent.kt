package com.lifeline.app.navigation

import com.arkivanov.decompose.ComponentContext
import com.lifeline.app.AppContainer

interface HomeComponent {
    // Home screen doesn't need a view model yet
}

class HomeComponentImpl(
    componentContext: ComponentContext,
    private val appContainer: AppContainer
) : HomeComponent, ComponentContext by componentContext

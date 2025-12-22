package com.lifeline.app.navigation

import com.arkivanov.decompose.ComponentContext
import com.lifeline.app.AppContainer
import com.lifeline.app.viewmodel.ServicesViewModel

interface ServicesComponent {
    val viewModel: ServicesViewModel
}

class ServicesComponentImpl(
    componentContext: ComponentContext,
    private val appContainer: AppContainer
) : ServicesComponent, ComponentContext by componentContext {
    override val viewModel: ServicesViewModel = appContainer.servicesViewModel
}

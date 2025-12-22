package com.lifeline.app.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.lifeline.app.AppContainer
import com.lifeline.app.domain.health.HealthTimelineEntry
import com.lifeline.app.domain.health.Symptom
import com.lifeline.app.viewmodel.HealthViewModel
import kotlinx.coroutines.flow.StateFlow

interface HealthComponent {
    val viewModel: HealthViewModel
}

class HealthComponentImpl(
    componentContext: ComponentContext,
    private val appContainer: AppContainer
) : HealthComponent, ComponentContext by componentContext {
    override val viewModel: HealthViewModel = appContainer.healthViewModel
}

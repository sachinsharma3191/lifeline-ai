package com.lifeline.app.navigation

import com.arkivanov.decompose.ComponentContext
import com.lifeline.app.AppContainer

interface HomeComponent {
    val moneyViewModel: com.lifeline.app.viewmodel.MoneyViewModel
    val healthViewModel: com.lifeline.app.viewmodel.HealthViewModel
    val learningViewModel: com.lifeline.app.viewmodel.LearningViewModel
}

class HomeComponentImpl(
    componentContext: ComponentContext,
    appContainer: AppContainer
) : HomeComponent, ComponentContext by componentContext {
    override val moneyViewModel = appContainer.moneyViewModel
    override val healthViewModel = appContainer.healthViewModel
    override val learningViewModel = appContainer.learningViewModel
}

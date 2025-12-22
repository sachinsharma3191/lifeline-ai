package com.lifeline.app.navigation

import com.arkivanov.decompose.ComponentContext
import com.lifeline.app.AppContainer
import com.lifeline.app.viewmodel.LearningViewModel

interface LearningComponent {
    val viewModel: LearningViewModel
}

class LearningComponentImpl(
    componentContext: ComponentContext,
    private val appContainer: AppContainer
) : LearningComponent, ComponentContext by componentContext {
    override val viewModel: LearningViewModel = appContainer.learningViewModel
}

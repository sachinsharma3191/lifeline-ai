package com.lifeline.app.navigation

import com.arkivanov.decompose.ComponentContext
import com.lifeline.app.viewmodel.MoneyViewModel

interface FinanceComponent {
    val viewModel: MoneyViewModel
}

class FinanceComponentImpl(
    componentContext: ComponentContext,
    private val appContainer: AppContainer
) : FinanceComponent, ComponentContext by componentContext {
    override val viewModel: MoneyViewModel = appContainer.moneyViewModel
}

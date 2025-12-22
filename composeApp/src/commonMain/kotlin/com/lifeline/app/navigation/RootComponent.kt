package com.lifeline.app.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.lifeline.app.AppContainer

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>
    
    fun onTabSelected(tab: Tab)
    
    sealed class Child {
        data class Home(val component: HomeComponent) : Child()
        data class Health(val component: HealthComponent) : Child()
        data class Finance(val component: FinanceComponent) : Child()
        data class Learning(val component: LearningComponent) : Child()
        data class Services(val component: ServicesComponent) : Child()
    }
    
    enum class Tab {
        HOME, HEALTH, FINANCE, LEARNING, SERVICES
    }
}

class RootComponentImpl(
    componentContext: ComponentContext,
    private val appContainer: AppContainer
) : RootComponent, ComponentContext by componentContext {
    
    private val navigation = StackNavigation<RootComponent.Tab>()
    
    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Tab.serializer(),
            initialConfiguration = RootComponent.Tab.HOME,
            handleBackButton = true,
            childFactory = ::child
        )
    
    override fun onTabSelected(tab: RootComponent.Tab) {
        navigation.replaceCurrent(tab)
    }
    
    private fun child(config: RootComponent.Tab, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            RootComponent.Tab.HOME -> RootComponent.Child.Home(
                HomeComponentImpl(componentContext, appContainer)
            )
            RootComponent.Tab.HEALTH -> RootComponent.Child.Health(
                HealthComponentImpl(componentContext, appContainer)
            )
            RootComponent.Tab.FINANCE -> RootComponent.Child.Finance(
                FinanceComponentImpl(componentContext, appContainer)
            )
            RootComponent.Tab.LEARNING -> RootComponent.Child.Learning(
                LearningComponentImpl(componentContext, appContainer)
            )
            RootComponent.Tab.SERVICES -> RootComponent.Child.Services(
                ServicesComponentImpl(componentContext, appContainer)
            )
        }
}

// Serializer for Tab enum
fun RootComponent.Tab.serializer() = object : StackNavigationSerializer<RootComponent.Tab> {
    override fun save(configuration: RootComponent.Tab): String = configuration.name
    override fun restore(data: String): RootComponent.Tab = RootComponent.Tab.valueOf(data)
}

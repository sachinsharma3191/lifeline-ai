package com.lifeline.app

import com.lifeline.app.ai.*
import com.lifeline.app.database.DatabaseDriverFactory
import com.lifeline.app.database.LifelineDatabase
import com.lifeline.app.repository.*
import com.lifeline.app.viewmodel.*
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Dependency injection container for the app
 */
class AppContainer(
    databaseDriverFactory: DatabaseDriverFactory,
    httpClientEngine: HttpClientEngine? = null
) {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    // Database
    private val databaseDriver = databaseDriverFactory.createDriver()
    val database: LifelineDatabase = LifelineDatabase(databaseDriver)
    
    // HTTP Client
    val httpClient: HttpClient = HttpClient(httpClientEngine ?: createHttpClientEngine()) {
        install(ContentNegotiation) {
            json()
        }
    }
    
    // AI Layer
    private val cloudAi: CloudAiClient? = null // Configure when API is available
    private val localNeuralAi: LocalNeuralAiClient = LocalNeuralAiClient()
    private val ruleBasedAi: RuleBasedAiClient = RuleBasedAiClient()
    
    val aiClient: AiClient = MultiLayerAiClient(
        cloudAi = cloudAi,
        localNeuralAi = localNeuralAi,
        ruleBasedAi = ruleBasedAi
    )
    
    // Repositories
    val healthRepository: HealthRepository = HealthRepositoryImpl(database)
    val financeRepository: FinanceRepository = FinanceRepositoryImpl(database)
    val learningRepository: LearningRepository = LearningRepositoryImpl(database)
    val servicesRepository: ServicesRepository = ServicesRepositoryImpl(database)
    
    // ViewModels
    val healthViewModel: HealthViewModel = HealthViewModel(healthRepository, aiClient, applicationScope)
    val moneyViewModel: MoneyViewModel = MoneyViewModel(financeRepository, aiClient, applicationScope)
    val learningViewModel: LearningViewModel = LearningViewModel(learningRepository, aiClient, applicationScope)
    val servicesViewModel: ServicesViewModel = ServicesViewModel(servicesRepository, aiClient, applicationScope)
    
    init {
        // Load local neural model in background
        applicationScope.launch {
            localNeuralAi.loadModel()
        }
    }
    
    fun cleanup() {
        httpClient.close()
        databaseDriver.close()
        localNeuralAi.unloadModel()
    }
}

expect fun createHttpClientEngine(): HttpClientEngine

package com.lifeline.app.ai

import kotlinx.coroutines.delay

/**
 * Rule-based AI client that always works offline
 */
class RuleBasedAiClient : AiClient {
    override suspend fun processRequest(prompt: String, context: Map<String, Any>): AiResponse {
        delay(50) // Simulate processing
        
        val response = when {
            prompt.contains("health", ignoreCase = true) -> 
                "I can help with health tracking. Try logging symptoms or checking your health timeline."
            prompt.contains("money", ignoreCase = true) || prompt.contains("finance", ignoreCase = true) -> 
                "I can assist with financial planning. Check your transactions or set financial goals."
            prompt.contains("learn", ignoreCase = true) || prompt.contains("education", ignoreCase = true) -> 
                "I can help with learning goals. Track your progress or discover new modules."
            prompt.contains("service", ignoreCase = true) || prompt.contains("community", ignoreCase = true) -> 
                "I can help you find community services. Search by category or location."
            else -> 
                "I'm here to help with health, finance, learning, and community services. What would you like to know?"
        }
        
        return AiResponse(
            text = response,
            confidence = 0.7f,
            source = AiSource.RULE_BASED,
            metadata = mapOf("type" to "rule_based")
        )
    }
    
    override suspend fun isAvailable(): Boolean = true
}
```

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/ai/CloudAiClient.kt
package com.lifeline.app.ai

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Cloud AI client using HTTP/LLM API
 */
class CloudAiClient(
    private val httpClient: HttpClient,
    private val apiUrl: String = "https://api.example.com/ai",
    private val apiKey: String? = null
) : AiClient {
    
    @Serializable
    private data class CloudRequest(
        val prompt: String,
        val context: Map<String, String> = emptyMap()
    )
    
    @Serializable
    private data class CloudResponse(
        val text: String,
        val confidence: Float = 1.0f
    )
    
    override suspend fun processRequest(prompt: String, context: Map<String, Any>): AiResponse {
        return try {
            val response = httpClient.post(apiUrl) {
                contentType(ContentType.Application.Json)
                apiKey?.let { header("Authorization", "Bearer $it") }
                setBody(CloudRequest(prompt, context.mapValues { it.value.toString() }))
            }.body<CloudResponse>()
            
            AiResponse(
                text = response.text,
                confidence = response.confidence,
                source = AiSource.CLOUD,
                metadata = mapOf("api" to "cloud")
            )
        } catch (e: Exception) {
            throw AiException("Cloud AI unavailable", e)
        }
    }
    
    override suspend fun isAvailable(): Boolean {
        return try {
            httpClient.head(apiUrl).status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }
}

class AiException(message: String, cause: Throwable? = null) : Exception(message, cause)
```

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/ai/MultiLayerAiClient.kt
package com.lifeline.app.ai

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Multi-layer AI client with fallback strategy:
 * 1. Try Cloud AI
 * 2. Try Local Neural AI (if available)
 * 3. Fallback to Rule-Based AI (always available)
 */
class MultiLayerAiClient(
    private val cloudAi: AiClient?,
    private val localNeuralAi: AiClient?,
    private val ruleBasedAi: AiClient = RuleBasedAiClient()
) : AiClient {
    
    override suspend fun processRequest(prompt: String, context: Map<String, Any>): AiResponse {
        // Try Cloud AI first
        cloudAi?.let { client ->
            if (client.isAvailable()) {
                return try {
                    client.processRequest(prompt, context)
                } catch (e: Exception) {
                    // Fall through to next layer
                }
            }
        }
        
        // Try Local Neural AI
        localNeuralAi?.let { client ->
            if (client.isAvailable()) {
                return try {
                    client.processRequest(prompt, context)
                } catch (e: Exception) {
                    // Fall through to rule-based
                }
            }
        }
        
        // Always fallback to rule-based
        return ruleBasedAi.processRequest(prompt, context)
    }
    
    override suspend fun isAvailable(): Boolean = true // Always available via rule-based
    
    /**
     * Process request with streaming responses (for future use)
     */
    fun processRequestStreaming(prompt: String, context: Map<String, Any> = emptyMap()): Flow<AiResponse> = flow {
        emit(processRequest(prompt, context))
    }
}
```

Step 4: Create domain models

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/domain/health/HealthModels.kt
package com.lifeline.app.domain.health

import kotlinx.datetime.Instant

data class Symptom(
    val id: String,
    val name: String,
    val severity: Int, // 1-10
    val timestamp: Instant,
    val notes: String? = null,
    val category: SymptomCategory
)

enum class SymptomCategory {
    PAIN,
    FATIGUE,
    MOOD,
    SLEEP,
    DIGESTIVE,
    RESPIRATORY,
    OTHER
}

data class HealthTimelineEntry(
    val id: String,
    val timestamp: Instant,
    val symptoms: List<Symptom>,
    val notes: String? = null
)
```

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/domain/finance/FinanceModels.kt
package com.lifeline.app.domain.finance

import kotlinx.datetime.Instant

data class Transaction(
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val timestamp: Instant,
    val description: String? = null,
    val tags: List<String> = emptyList()
)

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER
}

data class FinancialGoal(
    val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: Instant?,
    val category: String
)
```

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/domain/learning/LearningModels.kt
package com.lifeline.app.domain.learning

import kotlinx.datetime.Instant

data class LearningGoal(
    val id: String,
    val title: String,
    val description: String,
    val targetDate: Instant?,
    val progress: Float, // 0.0 to 1.0
    val status: GoalStatus
)

enum class GoalStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    PAUSED
}

data class LearningModule(
    val id: String,
    val title: String,
    val description: String,
    val content: String,
    val estimatedDuration: Int, // minutes
    val completed: Boolean = false,
    val completedAt: Instant? = null
)
```

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/domain/services/ServicesModels.kt
package com.lifeline.app.domain.services

data class CommunityService(
    val id: String,
    val name: String,
    val description: String,
    val category: ServiceCategory,
    val location: String?,
    val contactInfo: String?,
    val website: String?,
    val available: Boolean = true
)

enum class ServiceCategory {
    HEALTHCARE,
    MENTAL_HEALTH,
    FINANCIAL_ASSISTANCE,
    EDUCATION,
    HOUSING,
    FOOD_ASSISTANCE,
    LEGAL,
    OTHER
}
```

Step 5: Create repositories

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/repository/HealthRepository.kt
package com.lifeline.app.repository

import com.lifeline.app.domain.health.HealthTimelineEntry
import com.lifeline.app.domain.health.Symptom
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface HealthRepository {
    suspend fun addSymptom(symptom: Symptom)
    suspend fun getSymptoms(startDate: Instant?, endDate: Instant?): Flow<List<Symptom>>
    suspend fun addTimelineEntry(entry: HealthTimelineEntry)
    suspend fun getTimelineEntries(startDate: Instant?, endDate: Instant?): Flow<List<HealthTimelineEntry>>
    suspend fun deleteSymptom(id: String)
    suspend fun deleteTimelineEntry(id: String)
}
```

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/repository/FinanceRepository.kt
package com.lifeline.app.repository

import com.lifeline.app.domain.finance.FinancialGoal
import com.lifeline.app.domain.finance.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface FinanceRepository {
    suspend fun addTransaction(transaction: Transaction)
    suspend fun getTransactions(startDate: Instant?, endDate: Instant?): Flow<List<Transaction>>
    suspend fun addGoal(goal: FinancialGoal)
    suspend fun getGoals(): Flow<List<FinancialGoal>>
    suspend fun updateGoal(goal: FinancialGoal)
    suspend fun deleteTransaction(id: String)
    suspend fun deleteGoal(id: String)
}
```

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/repository/LearningRepository.kt
package com.lifeline.app.repository

import com.lifeline.app.domain.learning.LearningGoal
import com.lifeline.app.domain.learning.LearningModule
import kotlinx.coroutines.flow.Flow

interface LearningRepository {
    suspend fun addGoal(goal: LearningGoal)
    suspend fun getGoals(): Flow<List<LearningGoal>>
    suspend fun updateGoal(goal: LearningGoal)
    suspend fun deleteGoal(id: String)
    suspend fun addModule(module: LearningModule)
    suspend fun getModules(): Flow<List<LearningModule>>
    suspend fun completeModule(id: String)
}
```

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/repository/ServicesRepository.kt
package com.lifeline.app.repository

import com.lifeline.app.domain.services.CommunityService
import kotlinx.coroutines.flow.Flow

interface ServicesRepository {
    suspend fun getServices(category: String? = null): Flow<List<CommunityService>>
    suspend fun searchServices(query: String): Flow<List<CommunityService>>
    suspend fun addService(service: CommunityService)
    suspend fun updateService(service: CommunityService)
    suspend fun deleteService(id: String)
}
```

Step 6: Create ViewModels

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/viewmodel/HealthViewModel.kt
package com.lifeline.app.viewmodel

import com.lifeline.app.ai.AiClient
import com.lifeline.app.domain.health.HealthTimelineEntry
import com.lifeline.app.domain.health.Symptom
import com.lifeline.app.repository.HealthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class HealthViewModel(
    private val repository: HealthRepository,
    private val aiClient: AiClient
) {
    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()
    
    private val _symptoms = MutableStateFlow<List<Symptom>>(emptyList())
    val symptoms: StateFlow<List<Symptom>> = _symptoms.asStateFlow()
    
    private val _timelineEntries = MutableStateFlow<List<HealthTimelineEntry>>(emptyList())
    val timelineEntries: StateFlow<List<HealthTimelineEntry>> = _timelineEntries.asStateFlow()
    
    init {
        loadSymptoms()
        loadTimelineEntries()
    }
    
    fun addSymptom(symptom: Symptom) {
        _uiState.update { it.copy(isLoading = true) }
        repository.addSymptom(symptom)
        _uiState.update { it.copy(isLoading = false) }
    }
    
    fun addTimelineEntry(entry: HealthTimelineEntry) {
        _uiState.update { it.copy(isLoading = true) }
        repository.addTimelineEntry(entry)
        _uiState.update { it.copy(isLoading = false) }
    }
    
    fun askAi(prompt: String) {
        _uiState.update { it.copy(isLoading = true, aiResponse = null) }
        // In a real implementation, use coroutine scope
        // For now, this is the structure
    }
    
    private fun loadSymptoms() {
        repository.getSymptoms(null, null)
            .onEach { _symptoms.value = it }
            .launchIn(kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default))
    }
    
    private fun loadTimelineEntries() {
        repository.getTimelineEntries(null, null)
            .onEach { _timelineEntries.value = it }
            .launchIn(kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default))
    }
}

data class HealthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val aiResponse: String? = null
)
```

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/viewmodel/MoneyViewModel.kt
package com.lifeline.app.viewmodel

import com.lifeline.app.ai.AiClient
import com.lifeline.app.domain.finance.FinancialGoal
import com.lifeline.app.domain.finance.Transaction
import com.lifeline.app.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class MoneyViewModel(
    private val repository: FinanceRepository,
    private val aiClient: AiClient
) {
    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val _goals = MutableStateFlow<List<FinancialGoal>>(emptyList())
    val goals: StateFlow<List<FinancialGoal>> = _goals.asStateFlow()
    
    init {
        loadTransactions()
        loadGoals()
    }
    
    fun addTransaction(transaction: Transaction) {
        _uiState.update { it.copy(isLoading = true) }
        repository.addTransaction(transaction)
        _uiState.update { it.copy(isLoading = false) }
    }
    
    fun addGoal(goal: FinancialGoal) {
        _uiState.update { it.copy(isLoading = true) }
        repository.addGoal(goal)
        _uiState.update { it.copy(isLoading = false) }
    }
    
    fun updateGoal(goal: FinancialGoal) {
        _uiState.update { it.copy(isLoading = true) }
        repository.updateGoal(goal)
        _uiState.update { it.copy(isLoading = false) }
    }
    
    private fun loadTransactions() {
        repository.getTransactions(null, null)
            .onEach { _transactions.value = it }
            .launchIn(kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default))
    }
    
    private fun loadGoals() {
        repository.getGoals()
            .onEach { _goals.value = it }
            .launchIn(kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default))
    }
}

data class FinanceUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val aiResponse: String? = null
)
```

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/viewmodel/LearningViewModel.kt
package com.lifeline.app.viewmodel

import com.lifeline.app.ai.AiClient
import com.lifeline.app.domain.learning.LearningGoal
import com.lifeline.app.domain.learning.LearningModule
import com.lifeline.app.repository.LearningRepository
import kotlinx.coroutines.flow.*

class LearningViewModel(
    private val repository: LearningRepository,
    private val aiClient: AiClient
) {
    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()
    
    private val _goals = MutableStateFlow<List<LearningGoal>>(emptyList())
    val goals: StateFlow<List<LearningGoal>> = _goals.asStateFlow()
    
    private val _modules = MutableStateFlow<List<LearningModule>>(emptyList())
    val modules: StateFlow<List<LearningModule>> = _modules.asStateFlow()
    
    init {
        loadGoals()
        loadModules()
    }
    
    fun addGoal(goal: LearningGoal) {
        _uiState.update { it.copy(isLoading = true) }
        repository.addGoal(goal)
        _uiState.update { it.copy(isLoading = false) }
    }
    
    fun completeModule(id: String) {
        _uiState.update { it.copy(isLoading = true) }
        repository.completeModule(id)
        _uiState.update { it.copy(isLoading = false) }
    }
    
    private fun loadGoals() {
        repository.getGoals()
            .onEach { _goals.value = it }
            .launchIn(kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default))
    }
    
    private fun loadModules() {
        repository.getModules()
            .onEach { _modules.value = it }
            .launchIn(kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default))
    }
}

data class LearningUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val aiResponse: String? = null
)
```

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/viewmodel/ServicesViewModel.kt
package com.lifeline.app.viewmodel

import com.lifeline.app.ai.AiClient
import com.lifeline.app.domain.services.CommunityService
import com.lifeline.app.repository.ServicesRepository
import kotlinx.coroutines.flow.*

class ServicesViewModel(
    private val repository: ServicesRepository,
    private val aiClient: AiClient
) {
    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()
    
    private val _services = MutableStateFlow<List<CommunityService>>(emptyList())
    val services: StateFlow<List<CommunityService>> = _services.asStateFlow()
    
    init {
        loadServices()
    }
    
    fun searchServices(query: String) {
        _uiState.update { it.copy(isLoading = true, searchQuery = query) }
        repository.searchServices(query)
            .onEach { 
                _services.value = it
                _uiState.update { it.copy(isLoading = false) }
            }
            .launchIn(kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default))
    }
    
    fun filterByCategory(category: String?) {
        _uiState.update { it.copy(isLoading = true, selectedCategory = category) }
        repository.getServices(category)
            .onEach {
                _services.value = it
                _uiState.update { it.copy(isLoading = false) }
            }
            .launchIn(kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default))
    }
    
    private fun loadServices() {
        repository.getServices()
            .onEach { _services.value = it }
            .launchIn(kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default))
    }
}

data class ServicesUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null
)
```

Step 7: Create AppContainer

```kotlin:shared/src/commonMain/kotlin/com/lifeline/app/AppContainer.kt
package com.lifeline.app

import com.lifeline.app.ai.*
import com.lifeline.app.repository.*
import com.lifeline.app.viewmodel.*
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

/**
 * Dependency injection container for the app
 */
class AppContainer(
    httpClientEngine: HttpClientEngine? = null
) {
    // HTTP Client
    val httpClient: HttpClient = HttpClient(httpClientEngine ?: createHttpClientEngine()) {
        install(ContentNegotiation) {
            json()
        }
    }
    
    // AI Layer
    private val cloudAi: CloudAiClient? = null // Configure when API is available
    private val localNeuralAi: AiClient? = null // Configure when local models are ready
    private val ruleBasedAi: RuleBasedAiClient = RuleBasedAiClient()
    
    val aiClient: AiClient = MultiLayerAiClient(
        cloudAi = cloudAi,
        localNeuralAi = localNeuralAi,
        ruleBasedAi = ruleBasedAi
    )
    
    // Repositories (to be implemented with SQLDelight)
    val healthRepository: HealthRepository = TODO("Implement with SQLDelight")
    val financeRepository: FinanceRepository = TODO("Implement with SQLDelight")
    val learningRepository: LearningRepository = TODO("Implement with SQLDelight")
    val servicesRepository: ServicesRepository = TODO("Implement with SQLDelight")
    
    // ViewModels
    val healthViewModel: HealthViewModel = HealthViewModel(healthRepository, aiClient)
    val moneyViewModel: MoneyViewModel = MoneyViewModel(financeRepository, aiClient)
    val learningViewModel: LearningViewModel = LearningViewModel(learningRepository, aiClient)
    val servicesViewModel: ServicesViewModel = ServicesViewModel(servicesRepository, aiClient)
    
    fun cleanup() {
        httpClient.close()
    }
}

expect fun createHttpClientEngine(): HttpClientEngine
```

Step 8: Create platform-specific HTTP client engines

```kotlin:shared/src/androidMain/kotlin/com/lifeline/app/Platform.android.kt
package com.lifeline.app

import io.ktor.client.engine.android.Android

actual fun createHttpClientEngine() = Android.create()
```

```kotlin:shared/src/iosMain/kotlin/com/lifeline/app/Platform.ios.kt
package com.lifeline.app

import io.ktor.client.engine.darwin.Darwin

actual fun createHttpClientEngine() = Darwin.create()
```

```kotlin:shared/src/jvmMain/kotlin/com/lifeline/app/Platform.jvm.kt
package com.lifeline.app

import io.ktor.client.engine.java.Java

actual fun createHttpClientEngine() = Java.create()
```

```kotlin:shared/src/jsMain/kotlin/com/lifeline/app/Platform.js.kt
package com.lifeline.app

import io.ktor.client.engine.js.Js

actual fun createHttpClientEngine() = Js.create()
```

Step 9: Create SQLDelight schemas

```sql:shared/src/commonMain/sqldelight/health.sq
CREATE TABLE symptom (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    severity INTEGER NOT NULL,
    timestamp INTEGER NOT NULL,
    notes TEXT,
    category TEXT NOT NULL
);

CREATE TABLE health_timeline_entry (
    id TEXT PRIMARY KEY,
    timestamp INTEGER NOT NULL,
    notes TEXT
);

CREATE TABLE symptom_timeline_entry (
    symptom_id TEXT NOT NULL,
    timeline_entry_id TEXT NOT NULL,
    PRIMARY KEY (symptom_id, timeline_entry_id),
    FOREIGN KEY (symptom_id) REFERENCES symptom(id),
    FOREIGN KEY (timeline_entry_id) REFERENCES health_timeline_entry(id)
);

insertSymptom:
INSERT INTO symptom(id, name, severity, timestamp, notes, category)
VALUES (?, ?, ?, ?, ?, ?);

getAllSymptoms:
SELECT * FROM symptom
WHERE (?1 IS NULL OR timestamp >= ?1)
AND (?2 IS NULL OR timestamp <= ?2)
ORDER BY timestamp DESC;
```

```sql:shared/src/commonMain/sqldelight/finance.sq
CREATE TABLE transaction (
    id TEXT PRIMARY KEY,
    amount REAL NOT NULL,
    type TEXT NOT NULL,
    category TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    description TEXT,
    tags TEXT
);

CREATE TABLE financial_goal (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    target_amount REAL NOT NULL,
    current_amount REAL NOT NULL,
    deadline INTEGER,
    category TEXT NOT NULL
);

insertTransaction:
INSERT INTO transaction(id, amount, type, category, timestamp, description, tags)
VALUES (?, ?, ?, ?, ?, ?, ?);

getAllTransactions:
SELECT * FROM transaction
WHERE (?1 IS NULL OR timestamp >= ?1)
AND (?2 IS NULL OR timestamp <= ?2)
ORDER BY timestamp DESC;
```

This sets up the core architecture. Next steps:
1. Implement the repository classes with SQLDelight
2. Add the missing Ktor client engine dependencies
3. Add kotlinx-datetime dependency

Should I continue with the repository implementations and complete the SQLDelight setup?


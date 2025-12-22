package com.lifeline.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifeline.app.domain.finance.FinancialGoal
import com.lifeline.app.domain.finance.Transaction
import com.lifeline.app.domain.finance.TransactionType
import com.lifeline.app.navigation.FinanceComponent
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(component: FinanceComponent) {
    val viewModel = component.viewModel
    val uiState by viewModel.uiState.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val goals by viewModel.goals.collectAsState()
    
    var showAddTransactionDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = { showAddGoalDialog = true },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Add, "Add Goal")
                }
                FloatingActionButton(
                    onClick = { showAddTransactionDialog = true }
                ) {
                    Icon(Icons.Default.Add, "Add Transaction")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            
            Text(
                text = "Financial Goals",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(goals) { goal ->
                    GoalCard(goal)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions.take(10)) { transaction ->
                    TransactionCard(transaction)
                }
            }
        }
    }
    
    if (showAddTransactionDialog) {
        AddTransactionDialog(
            onDismiss = { showAddTransactionDialog = false },
            onAdd = { transaction ->
                viewModel.addTransaction(transaction)
                showAddTransactionDialog = false
            }
        )
    }
    
    if (showAddGoalDialog) {
        AddGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onAdd = { goal ->
                viewModel.addGoal(goal)
                showAddGoalDialog = false
            }
        )
    }
}

@Composable
fun TransactionCard(transaction: Transaction) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.titleMedium
                )
                transaction.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Text(
                text = "${if (transaction.type == TransactionType.EXPENSE) "-" else "+"}$${String.format("%.2f", transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = if (transaction.type == TransactionType.EXPENSE) 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun GoalCard(goal: FinancialGoal) {
    val progress = (goal.currentAmount / goal.targetAmount).coerceIn(0f, 1f)
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = goal.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$${String.format("%.2f", goal.currentAmount)} / $${String.format("%.2f", goal.targetAmount)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onAdd: (Transaction) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Transaction") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val transaction = Transaction(
                        id = java.util.UUID.randomUUID().toString(),
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        type = type,
                        category = category,
                        timestamp = Clock.System.now(),
                        description = description.ifEmpty { null }
                    )
                    onAdd(transaction)
                },
                enabled = amount.toDoubleOrNull() != null && category.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onAdd: (FinancialGoal) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Financial Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Goal Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it },
                    label = { Text("Target Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val goal = FinancialGoal(
                        id = java.util.UUID.randomUUID().toString(),
                        name = name,
                        targetAmount = targetAmount.toDoubleOrNull() ?: 0.0,
                        currentAmount = 0.0,
                        deadline = null,
                        category = category
                    )
                    onAdd(goal)
                },
                enabled = name.isNotBlank() && targetAmount.toDoubleOrNull() != null
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
```

```kotlin:composeApp/src/commonMain/kotlin/com/lifeline/app/ui/LearningScreen.kt
package com.lifeline.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifeline.app.domain.learning.LearningGoal
import com.lifeline.app.domain.learning.LearningModule
import com.lifeline.app.navigation.LearningComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(component: LearningComponent) {
    val viewModel = component.viewModel
    val uiState by viewModel.uiState.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val modules by viewModel.modules.collectAsState()
    
    var showAddGoalDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learning") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddGoalDialog = true }) {
                Icon(Icons.Default.Add, "Add Goal")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Learning Goals",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(goals) { goal ->
                    LearningGoalCard(goal)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Modules",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(modules) { module ->
                    ModuleCard(module) {
                        viewModel.completeModule(module.id)
                    }
                }
            }
        }
    }
    
    if (showAddGoalDialog) {
        AddLearningGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onAdd = { goal ->
                viewModel.addGoal(goal)
                showAddGoalDialog = false
            }
        )
    }
}

@Composable
fun LearningGoalCard(goal: LearningGoal) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = goal.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = goal.progress,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(goal.progress * 100).toInt()}% - ${goal.status.name}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ModuleCard(module: LearningModule, onComplete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = module.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = module.description,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${module.estimatedDuration} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!module.completed) {
                Button(onClick = onComplete) {
                    Text("Complete")
                }
            } else {
                Text(
                    text = "✓ Completed",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AddLearningGoalDialog(
    onDismiss: () -> Unit,
    onAdd: (LearningGoal) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Learning Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val goal = LearningGoal(
                        id = java.util.UUID.randomUUID().toString(),
                        title = title,
                        description = description,
                        targetDate = null,
                        progress = 0f,
                        status = com.lifeline.app.domain.learning.GoalStatus.NOT_STARTED
                    )
                    onAdd(goal)
                },
                enabled = title.isNotBlank() && description.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
```

```kotlin:composeApp/src/commonMain/kotlin/com/lifeline/app/ui/ServicesScreen.kt
package com.lifeline.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifeline.app.domain.services.CommunityService
import com.lifeline.app.navigation.ServicesComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(component: ServicesComponent) {
    val viewModel = component.viewModel
    val uiState by viewModel.uiState.collectAsState()
    val services by viewModel.services.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community Services") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchServices(it)
                },
                label = { Text("Search services...") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(services) { service ->
                    ServiceCard(service)
                }
            }
        }
    }
}

@Composable
fun ServiceCard(service: CommunityService) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = service.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = service.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = service.category.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            service.location?.let { location ->
                Text(
                    text = "📍 $location",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
```

```kotlin:composeApp/src/commonMain/kotlin/com/lifeline/app/App.kt
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
            AppContainer(
                databaseDriverFactory = databaseDriverFactory ?: createDatabaseDriverFactory(),
                httpClientEngine = null
            )
        }
        
        val rootComponent = remember {
            RootComponentImpl(componentContext, appContainer)
        }
        
        MainScreen(rootComponent)
    }
}

expect fun createDatabaseDriverFactory(): DatabaseDriverFactory
```

```kotlin:composeApp/src/androidMain/kotlin/com/lifeline/app/Platform.android.kt
package com.lifeline.app

import android.content.Context
import com.lifeline.app.database.DatabaseDriverFactory

actual fun createDatabaseDriverFactory(): DatabaseDriverFactory {
    val context = android.app.Application::class.java
        .getMethod("getApplicationContext")
        .invoke(null) as? Context
        ?: throw IllegalStateException("Cannot get Android context")
    return DatabaseDriverFactory(context)
}
```

```kotlin:composeApp/src/jvmMain/kotlin/com/lifeline/app/Platform.jvm.kt
package com.lifeline.app

import com.lifeline.app.database.DatabaseDriverFactory

actual fun createDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory()
}
```

```kotlin:composeApp/src/iosMain/kotlin/com/lifeline/app/Platform.ios.kt
package com.lifeline.app

import com.lifeline.app.database.DatabaseDriverFactory

actual fun createDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory()
}
```

```kotlin:composeApp/src/androidMain/kotlin/com/lifeline/app/MainActivity.kt
package com.lifeline.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
```

```kotlin:composeApp/src/jvmMain/kotlin/com/lifeline/app/main.kt
package com.lifeline.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "LifelineAI",
    ) {
        App()
    }
}
```

```kotlin:composeApp/src/commonMain/kotlin/com/lifeline/app/ai/LocalNeuralAiClient.kt
package com.lifeline.app.ai

import kotlinx.coroutines.delay

/**
 * Local Neural AI Client (stub for future ML model integration)
 * This would integrate with TensorFlow Lite, ONNX Runtime, or similar
 */
class LocalNeuralAiClient : AiClient {
    
    private var modelLoaded = false
    
    override suspend fun processRequest(prompt: String, context: Map<String, Any>): AiResponse {
        // Simulate model loading
        if (!modelLoaded) {
            delay(100)
            modelLoaded = true
        }
        
        // Simulate inference
        delay(200)
        
        // Placeholder: In real implementation, this would run inference on a local model
        val response = when {
            prompt.contains("health", ignoreCase = true) -> 
                "Based on your health data, I recommend tracking symptoms consistently and consulting healthcare providers for persistent issues."
            prompt.contains("money", ignoreCase = true) -> 
                "Your spending patterns suggest focusing on essential expenses. Consider setting up automatic savings."
            else -> 
                "I'm processing your request locally. For more detailed responses, ensure you're connected to the cloud AI service."
        }
        
        return AiResponse(
            text = response,
            confidence = 0.85f,
            source = AiSource.LOCAL_NEURAL,
            metadata = mapOf(
                "model" to "local_neural_v1",
                "inference_time_ms" to "200"
            )
        )
    }
    
    override suspend fun isAvailable(): Boolean {
        // In real implementation, check if model files exist and are loaded
        return true // Stub: always available
    }
    
    /**
     * Load the ML model (stub)
     */
    suspend fun loadModel(): Boolean {
        delay(500) // Simulate model loading
        modelLoaded = true
        return true
    }
    
    /**
     * Unload the model to free memory
     */
    fun unloadModel() {
        modelLoaded = false
    }
}
```

```kotlin:composeApp/src/commonMain/kotlin/com/lifeline/app/ai/MultiLayerAiClient.kt
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

```kotlin:composeApp/src/commonMain/kotlin/com/lifeline/app/AppContainer.kt
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
```

```kotlin:composeApp/src/commonMain/kotlin/com/lifeline/app/server/Application.kt
package com.lifeline.app

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

const val SERVER_PORT = 8080

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    
    routing {
        get("/") {
            call.respondText("LifelineAI Server API")
        }
        
        route("/api/v1") {
            route("/ai") {
                post("/chat") {
                    val request = call.receive<ChatRequest>()
                    // Simulate AI processing
                    val response = ChatResponse(
                        text = "This is a simulated AI response. In production, this would connect to an LLM API.",
                        confidence = 0.8f,
                        source = "cloud"
                    )
                    call.respond(response)
                }
                
                get("/health") {
                    call.respond(mapOf("status" to "ok", "service" to "ai"))
                }
            }
            
            route("/health") {
                get("/symptoms") {
                    call.respond(listOf<Map<String, Any>>())
                }
                
                get("/timeline") {
                    call.respond(listOf<Map<String, Any>>())
                }
            }
            
            route("/finance") {
                get("/transactions") {
                    call.respond(listOf<Map<String, Any>>())
                }
                
                get("/goals") {
                    call.respond(listOf<Map<String, Any>>())
                }
            }
            
            route("/learning") {
                get("/goals") {
                    call.respond(listOf<Map<String, Any>>())
                }
                
                get("/modules") {
                    call.respond(listOf<Map<String, Any>>())
                }
            }
            
            route("/services") {
                get {
                    call.respond(listOf<Map<String, Any>>())
                }
                
                get("/search") {
                    val query = call.request.queryParameters["q"] ?: ""
                    call.respond(listOf<Map<String, Any>>())
                }
            }
        }
    }
}

@Serializable
data class ChatRequest(
    val prompt: String,
    val context: Map<String, String> = emptyMap()
)

@Serializable
data class ChatResponse(
    val text: String,
    val confidence: Float,
    val source: String
)
```

```kotlin:composeApp/src/commonMain/kotlin/com/lifeline/app/server/build.gradle.kts
plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "com.lifeline.app"
version = "1.0.0"

application {
    mainClass.set("com.lifeline.app.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:${libs.versions.ktor.get()}")
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}
```


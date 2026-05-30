package com.lifeline.app.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.lifeline.app.config.AppConfigLoader
import com.lifeline.app.config.mapsUrl
import com.lifeline.app.domain.services.CommunityService
import com.lifeline.app.navigation.ServicesComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(component: ServicesComponent) {
    val config = remember { AppConfigLoader.get() }
    val screen = config.screens.services

    val viewModel = component.viewModel
    val uiState by viewModel.uiState.collectAsState()
    val services by viewModel.services.collectAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val uriHandler = LocalUriHandler.current
    
    var searchQuery by remember { mutableStateOf("") }
    var aiPrompt by remember { mutableStateOf("") }
    var selectedService by remember { mutableStateOf<CommunityService?>(null) }
    
    if (selectedService != null) {
        ServiceDetailScreen(
            service = selectedService!!,
            detailConfig = screen.detail,
            onBack = { selectedService = null },
            onOpenMaps = { address ->
                uriHandler.openUri(mapsUrl(config.servicesCatalog.mapsUrlTemplate, address))
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screen.title) },
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
                label = { Text(screen.searchPlaceholder) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                screen.aiSuggestions.forEach { suggestion ->
                    TextButton(onClick = {
                        aiPrompt = suggestion.prompt
                        viewModel.askAi(suggestion.prompt)
                    }) { Text(suggestion.label) }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = aiPrompt,
                onValueChange = { aiPrompt = it },
                label = { Text(screen.aiCoachLabel) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        if (aiPrompt.isNotBlank()) {
                            viewModel.askAi(aiPrompt)
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    if (aiPrompt.isNotBlank()) {
                        viewModel.askAi(aiPrompt)
                    }
                },
                enabled = aiPrompt.isNotBlank(),
                modifier = Modifier.align(androidx.compose.ui.Alignment.End)
            ) {
                Text("Ask")
            }

            uiState.aiResponse?.let { response ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = response,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (searchQuery.isBlank()) {
                Text(
                    text = screen.searchEmptyText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(services) { service ->
                        ServiceCard(
                            service = service,
                            onClick = { selectedService = service }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCard(service: CommunityService, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    service: CommunityService,
    detailConfig: com.lifeline.app.config.ServicesDetailConfig,
    onBack: () -> Unit,
    onOpenMaps: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(service.name) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(detailConfig.backLabel)
                    }
                },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = service.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "${detailConfig.categoryPrefix}${service.category.name}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            service.location?.let { address ->
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onOpenMaps(address) }
                )
            }

            service.contactInfo?.let {
                Text(text = "${detailConfig.contactPrefix}$it", style = MaterialTheme.typography.bodySmall)
            }
            service.website?.let {
                Text(text = "${detailConfig.websitePrefix}$it", style = MaterialTheme.typography.bodySmall)
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = detailConfig.mapDemoTitle,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Text(
                            text = detailConfig.mapPlaceholder,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

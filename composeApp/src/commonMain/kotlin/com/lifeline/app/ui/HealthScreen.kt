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
import com.lifeline.app.domain.health.Symptom
import com.lifeline.app.domain.health.SymptomCategory
import com.lifeline.app.navigation.HealthComponent
import com.lifeline.app.utils.randomUUID
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen(component: HealthComponent) {
    val viewModel = component.viewModel
    val uiState by viewModel.uiState.collectAsState()
    val symptoms by viewModel.symptoms.collectAsState()
    val timelineEntries by viewModel.timelineEntries.collectAsState()
    
    var showAddSymptomDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSymptomDialog = true }
            ) {
                Icon(Icons.Default.Add, "Add Symptom")
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
            
            uiState.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            uiState.aiResponse?.let { response ->
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Recent Symptoms",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(symptoms.take(10)) { symptom ->
                    SymptomCard(symptom)
                }
            }
        }
    }
    
    if (showAddSymptomDialog) {
        AddSymptomDialog(
            onDismiss = { showAddSymptomDialog = false },
            onAdd = { symptom ->
                viewModel.addSymptom(symptom)
                showAddSymptomDialog = false
            }
        )
    }
}

@Composable
fun SymptomCard(symptom: Symptom) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = symptom.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Severity: ${symptom.severity}/10",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = symptom.category.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            symptom.notes?.let { notes ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun AddSymptomDialog(
    onDismiss: () -> Unit,
    onAdd: (Symptom) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf(5) }
    var notes by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(SymptomCategory.OTHER) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Symptom") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Symptom Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Severity: $severity")
                Slider(
                    value = severity.toFloat(),
                    onValueChange = { severity = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8
                )
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val symptom = Symptom(
                        id = randomUUID(),
                        name = name,
                        severity = severity,
                        timestamp = currentTimestamp(),
                        notes = notes.ifEmpty { null },
                        category = category
                    )
                    onAdd(symptom)
                },
                enabled = name.isNotBlank()
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

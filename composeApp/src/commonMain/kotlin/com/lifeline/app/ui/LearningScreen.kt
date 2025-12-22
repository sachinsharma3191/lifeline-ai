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
import com.lifeline.app.utils.randomUUID

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
                        id = randomUUID(),
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


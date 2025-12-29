package com.lifeline.app.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
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

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var aiPrompt by remember { mutableStateOf("") }
    
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var goalToEdit by remember { mutableStateOf<LearningGoal?>(null) }
    
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = {
                    val p = "Learning progress"
                    aiPrompt = p
                    viewModel.askAi(p)
                }) { Text("Progress") }

                TextButton(onClick = {
                    val p = "Study plan"
                    aiPrompt = p
                    viewModel.askAi(p)
                }) { Text("Study plan") }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = aiPrompt,
                onValueChange = { aiPrompt = it },
                label = { Text("Ask AI (offline)") },
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

            Text(
                text = "Learning Goals",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(goals) { goal ->
                    LearningGoalCard(
                        goal = goal,
                        onEdit = { goalToEdit = it }
                    )
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

    goalToEdit?.let { existing ->
        EditLearningGoalDialog(
            goal = existing,
            onDismiss = { goalToEdit = null },
            onSave = { updated ->
                viewModel.updateGoal(updated)
                goalToEdit = null
            }
        )
    }
}

@Composable
fun LearningGoalCard(goal: LearningGoal, onEdit: (LearningGoal) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { onEdit(goal) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            }
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

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val addButtonFocusRequester = remember { FocusRequester() }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Learning Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            addButtonFocusRequester.requestFocus()
                        }
                    ),
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
                enabled = title.isNotBlank() && description.isNotBlank(),
                modifier = Modifier
                    .focusRequester(addButtonFocusRequester)
                    .focusable()
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
fun EditLearningGoalDialog(
    goal: LearningGoal,
    onDismiss: () -> Unit,
    onSave: (LearningGoal) -> Unit
) {
    var title by remember { mutableStateOf(goal.title) }
    var description by remember { mutableStateOf(goal.description) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val saveButtonFocusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Learning Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            saveButtonFocusRequester.requestFocus()
                        }
                    ),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        goal.copy(
                            title = title,
                            description = description
                        )
                    )
                },
                enabled = title.isNotBlank() && description.isNotBlank(),
                modifier = Modifier
                    .focusRequester(saveButtonFocusRequester)
                    .focusable()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


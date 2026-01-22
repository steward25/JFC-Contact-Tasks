package com.stewardapostol.jfc.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.stewardapostol.jfc.data.local.TaskWithNames
import com.stewardapostol.jfc.ui.viewmodel.MainViewModel

@Composable
fun TaskTabScreen(
    viewModel: MainViewModel,
    navController: NavController,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Open", "Completed")

    val openTasks by viewModel.openTasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> TaskList(openTasks, navController, viewModel::onToggleTaskStatusWithNames, viewModel::onDeleteTask)
            1 -> TaskList(completedTasks, navController, viewModel::onToggleTaskStatusWithNames, viewModel::onDeleteTask)
        }
    }
}

@Composable
fun TaskList(
    tasks: List<TaskWithNames>,
    navController: NavController, // --- FIXED: Added parameter ---
    onToggle: (TaskWithNames) -> Unit,
    onDelete: (Long) -> Unit
) {
    // Box used for the "Center no padding" requirement if the list is empty
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (tasks.isEmpty()) {
            Text("No tasks found", color = Color.Gray)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { it.task.taskId }) { taskWithNames ->
                    TaskItem(
                        taskWithNames = taskWithNames,
                        onClick = {
                            navController.navigate("task_details/${taskWithNames.task.taskId}")
                        },
                        onToggle = { onToggle(taskWithNames) },
                        onDelete = { onDelete(taskWithNames.task.taskId) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    taskWithNames: TaskWithNames,
    onClick: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = taskWithNames.task.title,
                    style = MaterialTheme.typography.titleMedium
                )
                val assignedTo = taskWithNames.businessName ?: taskWithNames.personName ?: "Unassigned"
                Text(
                    text = "For: $assignedTo",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Status Toggle Button
            Button(
                onClick = onToggle,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (taskWithNames.task.status == "Open")
                        Color(0xFF4CAF50) else Color(0xFF9E9E9E) // Grey for completed
                ),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text(
                    text = if (taskWithNames.task.status == "Open") "Complete" else "Re-open",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
            }
        }
    }
}
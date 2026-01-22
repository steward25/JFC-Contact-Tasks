package com.stewardapostol.jfc.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stewardapostol.jfc.ui.viewmodel.MainViewModel

@Composable
fun TaskDetailsScreen(
    taskId: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val tasks by viewModel.allTasks.collectAsState()
    val taskDetail = tasks.find { it.task.taskId == taskId }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        taskDetail?.let { detail ->
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = detail.task.title,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    
                    Text(
                        text = "For: ${detail.businessName ?: detail.personName ?: "General"}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = detail.task.description,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = onBack) {
                        Text("Close Details")
                    }
                }
            }
        } ?: Text("Task not found")
    }
}
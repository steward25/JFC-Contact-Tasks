package com.stewardapostol.jfc.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.stewardapostol.jfc.data.local.TaskWithNames

@Composable
fun TaskHistoryDialog(
    title: String,
    tasks: List<TaskWithNames>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(
            text = "Task History: $title",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        ) },
        text = {
            if (tasks.isEmpty()) {
                Text("No tasks found for this contact.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(tasks) { item ->
                        val task = item.task
                        val isDone = task.status == "Completed"

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDone)
                                    MaterialTheme.colorScheme.surfaceVariant
                                else
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // 1. THE ICON
                                    Icon(
                                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isDone) Color(0xFF4CAF50) else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // 2. THE STATUS BADGE (beside the logo)
                                    Surface(
                                        shape = MaterialTheme.shapes.extraSmall,
                                        color = if (isDone) Color(0xFFE8F5E9) else Color(0xFFEEEEEE),
                                    ) {
                                        Text(
                                            text = if (isDone) "COMPLETED" else "OPEN",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isDone) Color(0xFF2E7D32) else Color.DarkGray
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // 3. THE TITLE
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Show associated business if it exists
                                if (!item.businessName.isNullOrEmpty()) {
                                    // Increased padding to align with the text after Icon+Badge
                                    Text(
                                        text = "At: ${item.businessName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(start = 32.dp, top = 4.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
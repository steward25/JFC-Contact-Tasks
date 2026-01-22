package com.stewardapostol.jfc.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FloatingActionMenu(
    isMenuExpanded: Boolean,
    onToggleMenu: () -> Unit,
    onPersonClick: () -> Unit,
    onBusinessClick: () -> Unit,
    onTaskClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isMenuExpanded) {
            // --- ADD TASK ROW ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp
                ) {
                    Text(
                        "Add Task",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                SmallFloatingActionButton(
                    onClick = onTaskClick,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.Assignment, contentDescription = "Add Task")
                }
            }

            // Existing Business Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp
                ) {
                    Text("Add Business", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium)
                }
                Spacer(modifier = Modifier.width(8.dp))
                SmallFloatingActionButton(onClick = onBusinessClick, containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                    Icon(Icons.Default.Business, contentDescription = "Add Business")
                }
            }

            // Existing Person Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surfaceVariant, tonalElevation = 2.dp) {
                    Text("Add Person", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium)
                }
                Spacer(modifier = Modifier.width(8.dp))
                SmallFloatingActionButton(onClick = onPersonClick, containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                    Icon(Icons.Default.Person, contentDescription = "Add Person")
                }
            }
        }

        FloatingActionButton(
            onClick = onToggleMenu,
            containerColor = if (isMenuExpanded) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = if (isMenuExpanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = "Toggle Menu"
            )
        }
    }
}
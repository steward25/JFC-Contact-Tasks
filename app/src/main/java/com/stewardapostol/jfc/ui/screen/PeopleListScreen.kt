package com.stewardapostol.jfc.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.stewardapostol.jfc.data.local.PersonWithDetails
import com.stewardapostol.jfc.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PeopleListScreen(
    viewModel: MainViewModel,
    onEditPerson: (PersonWithDetails) -> Unit // Pass the detail DTO for editing
) {
    // 1. Observe the detailed list from ViewModel
    val people by viewModel.allPeopleWithDetails .collectAsState(initial = emptyList())

    Scaffold { padding ->
        if (people.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No people found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(people, key = { it.person.personId }) { detail ->
                    PersonItemCard(
                        detail = detail,
                        onEdit = { onEditPerson(detail) },
                        onDelete = { viewModel.onDeletePerson(detail.person.personId) }
                    )
                }
            }
        }
    }
}

@Composable
fun PersonItemCard(
    detail: PersonWithDetails,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "${detail.person.firstName} ${detail.person.lastName}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    // Display Business Name or "Independent"
                    Text(
                        text = detail.business?.name ?: "Independent",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit") }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete", tint = Color.Red) }
                }
            }

            // --- Contact Info ---
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "📧 ${detail.person.email}", style = MaterialTheme.typography.bodySmall)
            Text(text = "📞 ${detail.person.phoneNumber}", style = MaterialTheme.typography.bodySmall)

            // --- Tags ---
            if (detail.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    detail.tags.forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = { Text(tag.tagName) },
                            leadingIcon = { Icon(Icons.Default.Tag, null, Modifier.size(14.dp)) },
                        )
                    }
                }
            }
        }
    }
}
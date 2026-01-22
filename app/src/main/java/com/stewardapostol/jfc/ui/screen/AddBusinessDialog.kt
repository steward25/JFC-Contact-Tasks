package com.stewardapostol.jfc.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stewardapostol.jfc.data.local.Business
import com.stewardapostol.jfc.data.local.BusinessWithDetails
import com.stewardapostol.jfc.data.local.Category
import com.stewardapostol.jfc.data.local.Tag
import com.stewardapostol.jfc.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddBusinessDialog(
    viewModel: MainViewModel,
    existingBusiness: BusinessWithDetails? = null,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(existingBusiness?.business?.name ?: "") }
    var email by remember { mutableStateOf(existingBusiness?.business?.email ?: "") }

    val allCategories by viewModel.allCategories.collectAsState(initial = emptyList())
    // Initialize the list once based on the existing business
    val selectedCategories = remember {
        mutableStateListOf<Category>().apply {
            existingBusiness?.categories?.let { addAll(it) }
        }
    }

    val allTags by viewModel.allTags.collectAsState(initial = emptyList())
    val selectedTags = remember {
        mutableStateListOf<Tag>().apply {
            existingBusiness?.tags?.let { addAll(it) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingBusiness == null) "Add Business" else "Edit Business") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Business Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Contact Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Categories Section ---
                Text("Categories", style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allCategories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategories.any { it.categoryId == cat.categoryId },
                            onClick = {
                                if (selectedCategories.any { it.categoryId == cat.categoryId }) {
                                    selectedCategories.removeAll { it.categoryId == cat.categoryId }
                                } else {
                                    selectedCategories.add(cat)
                                }
                            },
                            label = { Text(cat.categoryName) }
                        )
                    }
                }

                // --- Tags Section ---
                Text("Tags", style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allTags.forEach { tag ->
                        FilterChip(
                            selected = selectedTags.any { it.tagId == tag.tagId },
                            onClick = {
                                if (selectedTags.any { it.tagId == tag.tagId }) {
                                    selectedTags.removeAll { it.tagId == tag.tagId }
                                } else {
                                    selectedTags.add(tag)
                                }
                            },
                            label = { Text(tag.tagName) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val business = Business(
                            businessId = existingBusiness?.business?.businessId ?: 0L,
                            name = name,
                            email = email
                        )
                        // Passing the actual List<Category> and List<Tag> as required by your ViewModel
                        viewModel.onSaveBusiness(
                            business = business,
                            categories = selectedCategories.toList(),
                            tags = selectedTags.toList()
                        )
                        onDismiss()
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.stewardapostol.jfc.data.local.Person
import com.stewardapostol.jfc.data.local.PersonWithDetails
import com.stewardapostol.jfc.data.local.Tag
import com.stewardapostol.jfc.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddPersonDialog(
    viewModel: MainViewModel,
    existingPerson: PersonWithDetails? = null,
    onDismiss: () -> Unit
) {
    // Basic Info State mapping your Data Class
    var firstName by remember { mutableStateOf(existingPerson?.person?.firstName ?: "") }
    var lastName by remember { mutableStateOf(existingPerson?.person?.lastName ?: "") }
    var email by remember { mutableStateOf(existingPerson?.person?.email ?: "") }
    var phoneNumber by remember { mutableStateOf(existingPerson?.person?.phoneNumber ?: "") }

    // Business Selection
    val businesses by viewModel.allBusinesses.collectAsState(initial = emptyList())
    var selectedBusiness by remember { mutableStateOf(existingPerson?.business) }
    var businessExpanded by remember { mutableStateOf(false) }

    // Tag Selection
    val allTags by viewModel.allTags.collectAsState(initial = emptyList())
    val selectedTags = remember {
        mutableStateListOf<Tag>().apply {
            existingPerson?.tags?.let { addAll(it) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingPerson == null) "Add Person" else "Edit Person") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Business Dropdown ---
                ExposedDropdownMenuBox(
                    expanded = businessExpanded,
                    onExpandedChange = { businessExpanded = !businessExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedBusiness?.name ?: "No Business (Independent)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Associated Business") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = businessExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = businessExpanded,
                        onDismissRequest = { businessExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Independent / None") },
                            onClick = { selectedBusiness = null; businessExpanded = false }
                        )
                        businesses.forEach { business ->
                            DropdownMenuItem(
                                text = { Text(business.name) },
                                onClick = { selectedBusiness = business; businessExpanded = false }
                            )
                        }
                    }
                }

                // --- Tags Selection ---
                Text("Tags", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    if (firstName.isNotBlank() && lastName.isNotBlank()) {
                        val person = Person(
                            personId = existingPerson?.person?.personId ?: 0L,
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            phoneNumber = phoneNumber,
                            businessId = selectedBusiness?.businessId
                        )

                        viewModel.onSavePerson(person, selectedTags.toList())
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
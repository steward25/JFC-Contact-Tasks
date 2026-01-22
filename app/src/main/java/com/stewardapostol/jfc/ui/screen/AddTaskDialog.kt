package com.stewardapostol.jfc.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.stewardapostol.jfc.data.local.Business
import com.stewardapostol.jfc.data.local.Person
import com.stewardapostol.jfc.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedBusiness by remember { mutableStateOf<Business?>(null) }
    var selectedPerson by remember { mutableStateOf<Person?>(null) }

    val businesses by viewModel.allBusinesses.collectAsState()
    val people by viewModel.allPeople.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank(),
                onClick = {
                    viewModel.onSaveTask(
                        title = title,
                        description = description,
                        businessId = selectedBusiness?.businessId,
                        personId = selectedPerson?.personId
                    )
                    onDismiss()
                }
            ) {
                Text("Save Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("New Task") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Text("Assign To:", style = MaterialTheme.typography.labelLarge)

                BusinessDropdown(
                    businesses = businesses,
                    selectedBusiness = selectedBusiness,
                    onBusinessSelected = { selectedBusiness = it; selectedPerson = null }
                )

                Text("— OR —",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                PersonDropdown(
                    people = people,
                    selectedPerson = selectedPerson,
                    onPersonSelected = { selectedPerson = it; selectedBusiness = null }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDropdown(
    businesses: List<Business>,
    selectedBusiness: Business?,
    onBusinessSelected: (Business) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }, // Simplified state toggle
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedBusiness?.name ?: "Select Business",
            onValueChange = {},
            readOnly = true,
            label = { Text("Assign to Business") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (businesses.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No businesses available") },
                    onClick = { expanded = false }
                )
            } else {
                businesses.forEach { business ->
                    DropdownMenuItem(
                        text = { Text(business.name) },
                        onClick = {
                            onBusinessSelected(business)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDropdown(
    people: List<Person>,
    selectedPerson: Person?,
    onPersonSelected: (Person) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }, // Simplified toggle
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = if (selectedPerson != null) "${selectedPerson.firstName} ${selectedPerson.lastName}" else "Select Person",
            onValueChange = {},
            readOnly = true,
            label = { Text("Assign to Person") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            // REQUIRED for Material 3 Dropdowns
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (people.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No people found", style = MaterialTheme.typography.bodySmall) },
                    onClick = { expanded = false },
                    enabled = false
                )
            } else {
                people.forEach { person ->
                    DropdownMenuItem(
                        text = { Text("${person.firstName} ${person.lastName}") },
                        onClick = {
                            onPersonSelected(person)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}
package com.stewardapostol.jfc.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stewardapostol.jfc.data.local.Business
import com.stewardapostol.jfc.data.local.Person
import com.stewardapostol.jfc.ui.viewmodel.MainViewModel

@Composable
fun AddTaskScreen(viewModel: MainViewModel, onTaskSaved: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var selectedBusiness by remember { mutableStateOf<Business?>(null) }
    var selectedPerson by remember { mutableStateOf<Person?>(null) }
    
    val businesses by viewModel.allBusinesses.collectAsState()
    val people by viewModel.allPeople.collectAsState()

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text("Create New Task", style = MaterialTheme.typography.headlineSmall)
        
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Task Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Business Selection Dropdown
        BusinessDropdown(
            businesses = businesses,
            selectedBusiness = selectedBusiness,
            onBusinessSelected = { 
                selectedBusiness = it
                selectedPerson = null // Task can only belong to one primary entity
            }
        )

        Text("OR", modifier = Modifier.padding(vertical = 8.dp).align(Alignment.CenterHorizontally))

        // Person Selection Dropdown
        PersonDropdown(
            people = people,
            selectedPerson = selectedPerson,
            onPersonSelected = { 
                selectedPerson = it
                selectedBusiness = null 
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                //viewModel.onSaveTask(title, selectedBusiness?.businessId, selectedPerson?.personId)
                onTaskSaved()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text("Save Task")
        }
    }
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
        onExpandedChange = { expanded = !expanded },
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
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = if (selectedPerson != null) "${selectedPerson.firstName} ${selectedPerson.lastName}" else "Select Person",
            onValueChange = {},
            readOnly = true,
            label = { Text("Assign to Person") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            people.forEach { person ->
                DropdownMenuItem(
                    text = { Text("${person.firstName} ${person.lastName}") },
                    onClick = {
                        onPersonSelected(person)
                        expanded = false
                    }
                )
            }
        }
    }
}
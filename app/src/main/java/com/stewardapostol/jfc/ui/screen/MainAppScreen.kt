package com.stewardapostol.jfc.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stewardapostol.jfc.ui.viewmodel.MainViewModel

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "tasks",
                    onClick = { navController.navigate("tasks") },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                    label = { Text("Tasks") }
                )
                NavigationBarItem(
                    selected = currentRoute == "businesses",
                    onClick = { navController.navigate("businesses") },
                    icon = { Icon(Icons.Default.Business, contentDescription = null) },
                    label = { Text("Businesses") }
                )
                NavigationBarItem(
                    selected = currentRoute == "people",
                    onClick = { navController.navigate("people") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("People") })

                NavigationBarItem(
                    selected = currentRoute == "management",
                    onClick = { navController.navigate("management") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Manage") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "tasks",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("tasks") {
                TaskTabScreen(viewModel, onAddTask = { /* Navigate to Add Task */ })
            }
            composable("businesses") {
                // Replace with your BusinessListScreen when ready
                Text("Business List Coming Soon", modifier = Modifier.padding(16.dp))
            }
            composable("people") {
                // Replace with your PeopleListScreen when ready
                Text("People List Coming Soon", modifier = Modifier.padding(16.dp))
            }
            composable("management") {
                ManagementScreen(viewModel = viewModel)
            }
        }
    }
}
package com.stewardapostol.jfc.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController


@Composable
fun BottomBar(currentRoute : String?, navController : NavHostController){
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
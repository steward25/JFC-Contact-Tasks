package com.stewardapostol.jfc.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.stewardapostol.jfc.data.local.BusinessWithDetails
import com.stewardapostol.jfc.data.local.PersonWithDetails
import com.stewardapostol.jfc.ui.navigation.Routes
import com.stewardapostol.jfc.ui.viewmodel.JWTAuthViewModel
import com.stewardapostol.jfc.ui.viewmodel.MainViewModel

@Composable
fun MainAppScreen(mainViewModel: MainViewModel, authViewModel: JWTAuthViewModel, onNavigateToAuth: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showFabMenu by remember { mutableStateOf(false) }
    var showPersonDialog by remember { mutableStateOf(false) }
    var personToEdit by remember { mutableStateOf<PersonWithDetails?>(null) }

    var showBusinessDialog by remember { mutableStateOf(false) }
    var businessToEdit by remember { mutableStateOf<BusinessWithDetails?>(null) }

    var showTaskDialog by remember { mutableStateOf(false) }

    var showUpdateProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    val loginState by authViewModel.loginState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(loginState) {
        if (loginState == "SUCCESS_PASSWORD_UPDATED") {
            Toast.makeText(context, "Security Alert: Password updated.", Toast.LENGTH_LONG).show()
            authViewModel.clearLoginState()
            showChangePasswordDialog = false
        }
    }

    Scaffold(
        topBar = {
            TopHeaderBar(
                authViewModel = authViewModel,
                mainViewModel = mainViewModel,
                onLogout = onNavigateToAuth,
                onUpdateProfile = { showUpdateProfileDialog = true },
                onChangePassword = { showChangePasswordDialog = true }
            )
        },
        bottomBar = { BottomBar(currentRoute, navController) },
        floatingActionButton = {
            if (currentRoute != Routes.MANAGEMENT) {
                FloatingActionMenu(
                    isMenuExpanded = showFabMenu,
                    onToggleMenu = { showFabMenu = !showFabMenu },
                    onPersonClick = {
                        showPersonDialog = true
                        showFabMenu = false
                    },
                    onBusinessClick = {
                        businessToEdit = null
                        showBusinessDialog = true
                        showFabMenu = false
                    },
                    onTaskClick = {
                        showTaskDialog = true
                        showFabMenu = false
                    }
                )
            }
        }
    ) { innerPadding ->
        // --- GLOBAL DIALOGS ---
        // Placing these here once ensures they don't reset during navigation
        if (showUpdateProfileDialog) {
            UpdateProfileDialog(
                viewModel = authViewModel,
                onDismiss = { showUpdateProfileDialog = false }
            )
        }

        if (showChangePasswordDialog) {
            LaunchedEffect(Unit) { authViewModel.clearLoginState() }

            ChangePasswordDialog(
                viewModel = authViewModel,
                onDismiss = {
                    showChangePasswordDialog = false
                    authViewModel.clearLoginState()
                }
            )
        }
        if (showPersonDialog) {
            AddPersonDialog(
                viewModel = mainViewModel,
                existingPerson = personToEdit,
                onDismiss = {
                    showPersonDialog = false
                    personToEdit = null

                    // REMOVE the 'if' check. If it's already there, launchSingleTop handles it.
                    navController.navigate(Routes.PEOPLE) {
                        popUpTo(Routes.TASKS) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        if (showBusinessDialog) {
            AddBusinessDialog(
                viewModel = mainViewModel,
                existingBusiness = businessToEdit,
                onDismiss = {
                    showBusinessDialog = false
                    businessToEdit = null

                    navController.navigate(Routes.BUSINESSES) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        if (showTaskDialog) {
            AddTaskDialog(
                viewModel = mainViewModel,
                onDismiss = { showTaskDialog = false }
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.TASKS,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Routes.TASKS) {
                    Box(modifier = Modifier.padding(innerPadding)) {
                        TaskTabScreen(
                            viewModel = mainViewModel,
                            navController = navController,
                        )
                    }
                }
                composable(
                    route = "task_details/{taskId}",
                    arguments = listOf(navArgument("taskId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
                    TaskDetailsScreen(
                        taskId = taskId,
                        viewModel = mainViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Routes.BUSINESSES) {
                    // Apply padding ONLY to the list so it doesn't hide behind the bars
                    Box(modifier = Modifier.padding(innerPadding)) {
                        BusinessListScreen(
                            viewModel = mainViewModel,
                            onEditBusiness = { detail ->
                                businessToEdit = detail
                                showBusinessDialog = true
                            }
                        )
                    }
                }
                composable(Routes.PEOPLE) {
                    Box(modifier = Modifier.padding(innerPadding)) {
                        PeopleListScreen(
                            viewModel = mainViewModel,
                            onEditPerson = { detail ->
                                personToEdit = detail
                                showPersonDialog = true
                            }
                        )
                    }
                }

                composable(Routes.MANAGEMENT) {
                    Box(modifier = Modifier.padding(innerPadding)) {
                        ManagementScreen(mainViewModel)
                    }
                }
                composable(Routes.PROFILE) {
                    ProfileScreen(
                        viewModel = authViewModel,
                        onAccountDeleted = {
                            navController.navigate(Routes.AUTH) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
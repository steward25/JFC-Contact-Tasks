package com.stewardapostol.jfc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stewardapostol.jfc.data.local.AppDatabase
import com.stewardapostol.jfc.data.repository.AppRepository
import com.stewardapostol.jfc.ui.auth.AuthScreen
import com.stewardapostol.jfc.ui.viewmodel.JWTAuthViewModel
import com.stewardapostol.jfc.ui.navigation.Routes
import com.stewardapostol.jfc.ui.screen.MainAppScreen
import com.stewardapostol.jfc.ui.theme.JFCContactAndTasksTheme
import com.stewardapostol.jfc.ui.viewmodel.MainViewModel
import com.stewardapostol.jfc.ui.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    lateinit var authViewModel : JWTAuthViewModel
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext)
        val repository = AppRepository(db.appDao())

        setContent {
            JFCContactAndTasksTheme {
                // Initialize Auth ViewModel
                authViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return JWTAuthViewModel(application) as T
                    }
                })

                // 2. Initialize the MainViewModel using the factory we created earlier
                mainViewModel = viewModel(factory = MainViewModelFactory(repository))

                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                val navController = rememberNavController()

                NavHost(navController, startDestination = if (isLoggedIn) Routes.MANAGEMENT else Routes.AUTH) {
                    composable(Routes.AUTH) {
                        AuthScreen(authViewModel) {
                            navController.navigate(Routes.MANAGEMENT) {
                                popUpTo(Routes.AUTH) { inclusive = true }
                            }
                        }
                    }
                    composable(Routes.MANAGEMENT) {
                        MainAppScreen(viewModel = mainViewModel)
                    }
                }
            }
        }
    }

}
package com.stewardapostol.jfc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stewardapostol.jfc.ui.auth.AuthScreen
import com.stewardapostol.jfc.ui.auth.JWTAuthViewModel
import com.stewardapostol.jfc.ui.navigation.Routes
import com.stewardapostol.jfc.ui.theme.JFCContactAndTasksTheme

class MainActivity : ComponentActivity() {

    lateinit var authViewModel : JWTAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JFCContactAndTasksTheme {
                authViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel(factory = object :
                        ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return JWTAuthViewModel(application) as T
                        }
                    })
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
                        Text(modifier = Modifier.background(Color.Red),
                            text = "Weather screen")
                    }
                }
            }
        }
    }
}
package com.dannav.mibancamit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dannav.mibancamit.presentation.HomeScreen
import com.dannav.mibancamit.presentation.LoginScreen
import com.dannav.mibancamit.presentation.MainViewModel
import com.dannav.mibancamit.presentation.RegisterScreen
import com.dannav.mibancamit.presentation.home.MainScreen
import com.dannav.mibancamit.presentation.login.Login
import com.dannav.mibancamit.presentation.register.Register
import com.dannav.mibancamit.ui.theme.MiBancaMITTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiBancaMITTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent(
    mainViewModel : MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val startDestination = HomeScreen

    NavHost(navController = navController, startDestination = startDestination) {
        composable<LoginScreen> {
            Login(onRegisterClick = {
                navController.navigate(RegisterScreen)
            }) {
                navController.navigate(HomeScreen) {
                    popUpTo(LoginScreen) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }

        composable<RegisterScreen> {
            Register(onLoginClick = {
                navController.popBackStack()
            }) {
                navController.navigate(HomeScreen) {
                    popUpTo(LoginScreen) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }

        composable<HomeScreen> {
            MainScreen {
                mainViewModel.logOut()
                navController.navigate(LoginScreen) {
                    popUpTo(HomeScreen) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }
}
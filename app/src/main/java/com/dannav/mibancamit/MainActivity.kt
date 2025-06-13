package com.dannav.mibancamit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dannav.mibancamit.presentation.HomeScreen
import com.dannav.mibancamit.presentation.LoginScreen
import com.dannav.mibancamit.presentation.RegisterScreen
import com.dannav.mibancamit.presentation.components.cards.NeomorphismCard
import com.dannav.mibancamit.presentation.login.Login
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
fun MainContent() {
    val navController = rememberNavController()
    val startDestination = LoginScreen

    NavHost( navController = navController, startDestination = startDestination){
        composable<LoginScreen> {
            Login (onRegisterClick = {
                navController.navigate(RegisterScreen)
            }){
                navController.navigate(HomeScreen){
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        }

        composable<RegisterScreen> {
            Login (onRegisterClick = {
                navController.popBackStack()
            }){
                navController.navigate(HomeScreen){
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        }
        composable<HomeScreen> {
            NeomorphismCard {

            }
        }
    }
}
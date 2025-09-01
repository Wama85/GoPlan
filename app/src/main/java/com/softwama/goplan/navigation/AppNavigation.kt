package com.softwama.goplan.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.softwama.goplan.features.login.presentation.LoginScreen
import com.softwama.goplan.features.profile.presentation.ProfileScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // Navegar a Profile cuando el login sea exitoso
                    navController.navigate(Screen.Profile.route) {
                        // Limpiamos el back stack para que no se pueda volver al login con back button
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

    }
}
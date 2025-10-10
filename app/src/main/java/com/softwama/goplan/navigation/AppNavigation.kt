package com.softwama.goplan.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.softwama.goplan.features.login.presentation.LoginScreen
import com.softwama.goplan.features.suscribe.presentation.SuscribeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // Pantalla de login
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // ✅ Después del login, abrimos MainApp (que contiene el Drawer)
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSuscribe = {
                    navController.navigate(Screen.Suscribe.route)
                }
            )
        }

        // ✅ En lugar de DashboardScreen directo, cargamos MainApp (Drawer + Dashboard interno)
        composable(Screen.Dashboard.route) {
            MainApp(navController = navController)
        }

        composable(Screen.Suscribe.route) {
            SuscribeScreen(navController = navController)
        }
    }
}

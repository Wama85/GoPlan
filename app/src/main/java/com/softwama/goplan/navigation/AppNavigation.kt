package com.softwama.goplan.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.softwama.goplan.features.calendar.presentation.CalendarScreen
import com.softwama.goplan.features.dashboard.DashboardScreen
import com.softwama.goplan.features.login.presentation.LoginScreen
import com.softwama.goplan.features.profile.presentation.ProfileScreen
import com.softwama.goplan.features.suscribe.presentation.SuscribeScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        // Pantalla de Login
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSuscribe = {
                    navController.navigate(Screen.Suscribe.route)
                }
            )
        }

        // Pantalla de Registro
        composable(Screen.Suscribe.route) {
            SuscribeScreen(navController = navController)
        }

        // Pantalla de Dashboard
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        // Pantalla de Perfil
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        // Pantalla de Calendario
        composable(Screen.Calendar.route) {
            CalendarScreen(navController = navController)
        }
    }
}
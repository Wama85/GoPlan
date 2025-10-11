package com.softwama.goplan.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.features.calendar.presentation.CalendarScreen
import com.softwama.goplan.features.dashboard.DashboardScreen
import com.softwama.goplan.features.login.presentation.LoginScreen
import com.softwama.goplan.core.notifications.presentation.NotificationsScreen
import com.softwama.goplan.features.profile.presentation.ProfileScreen
import com.softwama.goplan.features.suscribe.presentation.SuscribeScreen
import com.softwama.goplan.features.maintenance.presentation.MaintenanceScreen
import com.softwama.goplan.features.maintenance.presentation.MaintenanceViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val dataStore: UserPreferencesDataStore = koinInject()
    val maintenanceViewModel: MaintenanceViewModel = koinViewModel()

    var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        maintenanceViewModel.checkAppStatus()
        isLoggedIn = dataStore.getLoginStatus().first()
    }

    val isMaintenance by maintenanceViewModel.isMaintenance.collectAsState()

    when {
        isMaintenance == null || isLoggedIn == null -> {
            LoadingSplash()
        }
        isMaintenance == true -> {
            MaintenanceScreen()
        }
        else -> {
            val startDestination = if (isLoggedIn == true) "dashboard" else "login"

            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = modifier
            ) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onNavigateToSuscribe = {
                            navController.navigate("suscribe")
                        }
                    )
                }

                composable("suscribe") {
                    SuscribeScreen(navController = navController)
                }

                composable("dashboard") {
                    DashboardScreen(navController = navController)
                }

                composable("profile") {
                    ProfileScreen(navController = navController)
                }

                composable("calendar") {
                    CalendarScreen(navController = navController)
                }

                composable("notifications") {
                    NotificationsScreen(navController = navController)
                }
            }
        }
    }
}

@Composable
fun LoadingSplash() {
    var dots by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dots = when (dots.length) {
                0 -> "."
                1 -> ".."
                2 -> "..."
                else -> ""
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }
}
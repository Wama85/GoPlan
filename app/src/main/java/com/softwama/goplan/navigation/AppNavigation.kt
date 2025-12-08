package com.softwama.goplan.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.features.calendar.presentation.CalendarScreen
import com.softwama.goplan.features.dashboard.DashboardScreen
import com.softwama.goplan.features.estadisticas.presentation.EstadisticasScreen
import com.softwama.goplan.features.login.presentation.LoginScreen
import com.softwama.goplan.features.maintenance.presentation.MaintenanceScreen
import com.softwama.goplan.features.maintenance.presentation.MaintenanceViewModel
import com.softwama.goplan.features.profile.presentation.EditProfileScreen
import com.softwama.goplan.features.profile.presentation.ProfileScreen
import com.softwama.goplan.features.profile.presentation.SettingsScreen
import com.softwama.goplan.features.proyectos.presentation.ProyectosScreen
import com.softwama.goplan.features.suscribe.presentation.SuscribeScreen
import com.softwama.goplan.features.tareas.presentation.TareasScreen
import com.softwama.goplan.core.notifications.presentation.NotificationsScreen
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
    val lifecycleOwner = LocalLifecycleOwner.current

    var initialLoginState by remember { mutableStateOf<Boolean?>(null) }

    // Cargar si está logueado
    LaunchedEffect(Unit) {
        maintenanceViewModel.checkAppStatus()
        initialLoginState = dataStore.getLoginStatus().first()
    }

    // Re-check mantenimiento cuando la app vuelve al foreground
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            maintenanceViewModel.checkAppStatus()
        }
    }

    val isMaintenance by maintenanceViewModel.isMaintenance.collectAsState()

    // Manejo de mantenimiento
    LaunchedEffect(isMaintenance) {
        if (isMaintenance == true) {
            maintenanceViewModel.startPolling()
        } else {
            maintenanceViewModel.stopPolling()
        }
    }

    when {
        isMaintenance == null || initialLoginState == null -> {
            LoadingSplash()
        }

        isMaintenance == true -> {
            MaintenanceScreen()
        }

        else -> {
            val startDestination = if (initialLoginState == true) "dashboard" else "login"

            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = modifier
            ) {

                // LOGIN
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

                // SUSCRIBE
                composable("suscribe") {
                    SuscribeScreen(navController = navController)
                }

                // DASHBOARD
                composable("dashboard") {
                    DashboardScreen(navController = navController)
                }

                // PROFILE
                composable("profile") {
                    ProfileScreen(navController = navController)
                }

                // CALENDARIO
                composable("calendar") {
                    CalendarScreen(navController = navController)
                }

                // NOTIFICACIONES
                composable("notifications") {
                    NotificationsScreen(navController = navController)
                }

                // EDITAR PERFIL
                composable("editProfile") {
                    EditProfileScreen(navController)
                }

                // SETTINGS
                composable("settingsScreen") {
                    SettingsScreen(navController)
                }

                // TAREAS
                composable("tareas") {
                    TareasScreen(navController = navController)
                }

                // PROYECTOS
                composable("proyectos") {
                    ProyectosScreen(navController = navController)
                }

                // ESTADÍSTICAS
                composable("estadisticas") {
                    EstadisticasScreen(navController = navController)
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

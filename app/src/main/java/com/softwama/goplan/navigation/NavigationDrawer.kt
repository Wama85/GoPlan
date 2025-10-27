package com.softwama.goplan.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationDrawer(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
) {
    data object Dashboard : NavigationDrawer(
        "Dashboard",
        Icons.Filled.Home,
        Icons.Outlined.Home,
        Screen.Dashboard.route
    )

    data object Tareas : NavigationDrawer(
        "Tareas",
        Icons.Filled.Task,
        Icons.Outlined.Task,
        Screen.Tareas.route
    )

    data object Proyectos : NavigationDrawer(
        "Proyectos",
        Icons.Filled.Folder,
        Icons.Outlined.Folder,
        Screen.Proyectos.route
    )

    data object Calendar : NavigationDrawer(
        "Calendario",
        Icons.Filled.CalendarToday,
        Icons.Outlined.CalendarToday,
        Screen.Calendar.route
    )

    data object Estadisticas : NavigationDrawer(
        "Estadísticas",
        Icons.Filled.BarChart,
        Icons.Outlined.BarChart,
        Screen.Estadisticas.route
    )

    data object CerrarSesion : NavigationDrawer(
        "Cerrar Sesión",
        Icons.Filled.Logout,
        Icons.Outlined.Logout,
        "logout"
    )
}
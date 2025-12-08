package com.softwama.goplan.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.softwama.goplan.R

sealed class NavigationDrawer(
    @StringRes val label: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
) {

    data object Dashboard : NavigationDrawer(
        R.string.dashboard_title,
        Icons.Filled.Home,
        Icons.Outlined.Home,
        Screen.Dashboard.route
    )

    data object Tareas : NavigationDrawer(
        R.string.dashboard_tasks,
        Icons.Filled.Task,
        Icons.Outlined.Task,
        Screen.Tareas.route
    )

    data object Proyectos : NavigationDrawer(
        R.string.dashboard_projects,
        Icons.Filled.Folder,
        Icons.Outlined.Folder,
        Screen.Proyectos.route
    )

    data object Calendar : NavigationDrawer(
        R.string.dashboard_calendar,
        Icons.Filled.CalendarToday,
        Icons.Outlined.CalendarToday,
        Screen.Calendar.route
    )

    data object Estadisticas : NavigationDrawer(
        R.string.dashboard_stats,
        Icons.Filled.BarChart,
        Icons.Outlined.BarChart,
        Screen.Estadisticas.route
    )

    data object CerrarSesion : NavigationDrawer(
        R.string.drawer_logout,
        Icons.Filled.Logout,
        Icons.Outlined.Logout,
        "logout"
    )
}

package com.softwama.goplan.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
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






}
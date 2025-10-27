package com.softwama.goplan.navigation

sealed class Screen(val route: String) {
   object Login : Screen("login")
   object Profile : Screen("profile")
   object Suscribe : Screen("suscribe")
   object Dashboard : Screen("dashboard")
   object Calendar : Screen("calendar")
   object Notifications : Screen("notifications")
   object Estadisticas :Screen("estadisticas")
   object Proyectos :Screen("proyectos")
   object Tareas :Screen("tareas")
   object EditProfile : Screen("editProfile")
   object SettingsScreen : Screen("settingsScreen")


}
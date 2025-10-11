package com.softwama.goplan.navigation

sealed class Screen(val route: String) {
   object Login : Screen("login")
   object Profile : Screen("profile")
   object Suscribe : Screen("suscribe")
   object Dashboard : Screen("dashboard")
   object Calendar : Screen("calendar")

}
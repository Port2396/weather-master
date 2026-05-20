package com.whatstheweather.app.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CityManager : Screen("city_manager")
    object Settings : Screen("settings")
}

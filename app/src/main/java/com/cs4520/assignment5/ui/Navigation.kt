package com.cs4520.assignment5.ui

import LoginScreen
import ProductListScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

enum class Screen {
    LOGIN,
    PRODUCT_LIST
}

sealed class NavigationItem(val route: String) {
    data object Login : NavigationItem(Screen.LOGIN.name)
    data object ProductList : NavigationItem(Screen.PRODUCT_LIST.name)
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationItem.Login.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.Login.route) {
            LoginScreen(navController)
        }
        composable(NavigationItem.ProductList.route) {
            ProductListScreen()
        }
    }
}
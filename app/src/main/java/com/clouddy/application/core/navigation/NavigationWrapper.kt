package com.clouddy.application.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clouddy.application.ui.screen.LoginScreen
import com.clouddy.application.ui.screen.RegistroScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Login ) {
        composable<Login> {
            LoginScreen { navController.navigate(Registro) }
        }

        composable<Registro> {
            RegistroScreen { navController.navigate(Login) {
                popUpTo(Login) { inclusive = true }
            } }
        }

    }

}
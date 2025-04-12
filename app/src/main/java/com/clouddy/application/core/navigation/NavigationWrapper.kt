package com.clouddy.application.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clouddy.application.ui.screen.home.HomeScreen
import com.clouddy.application.ui.screen.login.LoginScreen
import com.clouddy.application.ui.screen.login.RegistroScreen
import com.clouddy.application.ui.viewModel.AuthVM

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val authVM: AuthVM = viewModel()
    NavHost(navController = navController, startDestination = Login ) {
        composable<Login> {
            LoginScreen (navigateHome = { navController.navigate(Home) },
                navigateToRegistro = { navController.navigate(Registro) },
                authVM = authVM
            )
        }

        composable<Home> {
            HomeScreen (
                navigateToLogin = {
                    navController.navigate(Login)},
                authVM = authVM
            )
        }

        composable<Registro> {
            RegistroScreen(
                navigateHome = {
                    navController.navigate(Home) },
                navigateToLogin = { navController.navigate(Login) },
                authVM = authVM
            )
        }
    }
}
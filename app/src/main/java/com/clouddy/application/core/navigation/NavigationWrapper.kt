package com.clouddy.application.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clouddy.application.ui.screen.calendar.screen.DayScreen
import com.clouddy.application.ui.screen.calendar.screen.FullScreenCalendar
import com.clouddy.application.ui.screen.login.screen.LoginScreen
import com.clouddy.application.ui.screen.login.screen.RegistroScreen
import com.clouddy.application.ui.screen.home.HomeScreen
import com.clouddy.application.ui.screen.login.viewModel.AuthVM
import com.clouddy.application.ui.screen.notes.NotesApp
import com.clouddy.application.ui.screen.pomodoro.screen.PomodoroList
import com.clouddy.application.ui.screen.pomodoro.screen.PomodoroScreen
import com.clouddy.application.ui.screen.toDo.screen.TaskScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val authVM: AuthVM = viewModel()
    NavHost(navController = navController, startDestination = Login ) {
        composable<Login> {
            LoginScreen (navigateHome = { navController.navigate(Home) },
                navigateToRegistro = { navController.navigate(Registro) },
            )
        }

        composable<Home> {
            HomeScreen (
                navigateToLogin = {
                    navController.navigate(Login)},
                navigateToNotes = { navController.navigate(Notes) },
                navigateToPomodoro = { navController.navigate(Pomodoro) },
                navigateToTask = { navController.navigate(Task)  } ,
                navigateToCalendar = { navController.navigate(Calendar)  }
            )
        }

        composable<Registro> {
            RegistroScreen(
                navigateHome = {
                    navController.navigate(Home) },
                navigateToLogin = { navController.navigate(Login) },
            )
        }

        composable<Notes> {
            NotesApp( navigateToTask = {navController.navigate(Task)})
        }

        composable<Task> {
            TaskScreen( navigateToNotesScreen = {navController.navigate(Notes)})
        }

        composable<Pomodoro> {
            PomodoroList( navigateToPomodoroScreen = {navController.navigate(PomodoroScreen)})
        }

        composable<PomodoroScreen> {
            PomodoroScreen()
        }

        composable<Calendar> {
            FullScreenCalendar( navigateToDayScreen = { navController.navigate(DayScreen) })
        }

        composable<DayScreen> { backStackEntry ->
            DayScreen(  )

        }

    }
}
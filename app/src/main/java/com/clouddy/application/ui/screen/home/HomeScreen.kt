package com.clouddy.application.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.clouddy.application.ui.screen.login.viewModel.AuthState
import com.clouddy.application.ui.screen.login.viewModel.AuthVM
import com.example.clouddy.ui.theme.ClouddyTheme

@Composable
fun HomeScreen(navigateToLogin: () -> Unit, authVM: AuthVM) {
    ClouddyTheme {
        val authState = authVM.authState.observeAsState()

        LaunchedEffect(authState.value) {
            if (authState.value is AuthState.Unauthenticated) {
                navigateToLogin()
            }
        }

        Scaffold(
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF4684BA),
                                    Color(0xFF13DCF2)
                                )
                            )
                        )
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Home page")

                        TextButton(onClick = { authVM.signOut() }) {
                            Text(text = "Sign Out")
                        }
                    }
                }
            }
        )
    }
}

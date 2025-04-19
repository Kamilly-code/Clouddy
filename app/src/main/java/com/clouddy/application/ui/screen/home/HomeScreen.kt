package com.clouddy.application.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.clouddy.application.R
import com.clouddy.application.ui.screen.login.viewModel.AuthState
import com.clouddy.application.ui.screen.login.viewModel.AuthVM
import com.example.clouddy.ui.theme.ClouddyTheme

@Composable
fun HomeScreen(navigateToLogin: () -> Unit, authVM: AuthVM,
               navigateToNotes: () -> Unit, navigateToTask: () -> Unit) {
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
                                    Color(0xFF9DABC8),
                                    Color(0xFFCAC7E6),
                                    Color(0xFFB6D4F0),
                                    Color(0xFF83A6D0)
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

                        Button(
                            onClick = { navigateToNotes() }
                        ) { Text(text = "Notes") }

                        Button(
                            onClick = { navigateToTask() }
                        ) { Text(text = "Task") }

                        Image(
                            painter = painterResource(id = R.drawable.ballerina_icon),
                            contentDescription = null,
                            modifier = Modifier.size(450.dp)
                        )

                    }


                }
            }
        )
    }
}






package com.clouddy.application.ui.screen.login.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clouddy.application.R
import com.clouddy.application.domain.usecase.AuthState
import com.clouddy.application.ui.screen.login.components.CloudImageWithShadow
import com.clouddy.application.ui.screen.login.viewModel.AuthVM
import com.example.clouddy.ui.theme.ClouddyTheme
import com.example.clouddy.ui.theme.HoltwoodOneSC
import com.example.clouddy.ui.theme.Iceland
import com.example.clouddy.ui.theme.LoginColor



@Composable
fun LoginScreen(navigateHome: () -> Unit , navigateToRegistro: () -> Unit) {
    ClouddyTheme {
        val authVM: AuthVM = hiltViewModel()
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        var passwordVisible by remember { mutableStateOf(false) }

        val authState = authVM.authState.observeAsState()
        val context = LocalContext.current

        LaunchedEffect(authState.value) {
            when (authState.value) {
                is AuthState.Authenticated -> navigateHome()
                is AuthState.Error -> {
                    Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
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
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.back_montana),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(y = 140.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.front_montana),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(y = 185.dp)
                        )

                        CloudImageWithShadow(imageRes = R.drawable.nube_central, modifier = Modifier.height(180.dp).offset(y = 20.dp))
                        CloudImageWithShadow(imageRes = R.drawable.nube_5c, modifier = Modifier.height(150.dp).offset(x = (-120).dp, y = 165.dp))
                        CloudImageWithShadow(imageRes = R.drawable.nube_6r, modifier = Modifier.height(165.dp).offset(x = 130.dp, y = 170.dp))
                        CloudImageWithShadow(imageRes = R.drawable.nube_6c, modifier = Modifier.height(150.dp).offset(x = (-135).dp, y = 295.dp))
                        CloudImageWithShadow(imageRes = R.drawable.nube_6r, modifier = Modifier.height(165.dp).offset(x = 145.dp, y = 330.dp))
                        CloudImageWithShadow(imageRes = R.drawable.nube_6c, modifier = Modifier.height(165.dp).offset(x = (-130).dp, y = 440.dp))
                        CloudImageWithShadow(imageRes = R.drawable.nube_5c, modifier = Modifier.height(155.dp).offset(x = 110.dp, y = 500.dp))
                        CloudImageWithShadow(imageRes = R.drawable.nube_6c, modifier = Modifier.height(145.dp).offset(x = (-110).dp, y = 590.dp))

                        Image(
                            painter = painterResource(id = R.drawable.livro1),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(y = 360.dp)
                        )

                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF074E8C).copy(alpha = 0.2f),
                                        Color(0xFF0A0A0D).copy(alpha = 0.2f),
                                    )
                                )

                            )) {}

                        Column (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 170.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,

                        ) {
                            Text(
                                text = "LOGIN",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = HoltwoodOneSC,
                                    fontSize = 25.sp
                                ),
                                color = LoginColor
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Email",
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = Iceland,
                                color = Color.Black,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(horizontal = 35.dp)
                            )
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 30.dp)
                                    .heightIn(max = 56.dp),
                                value = email,
                                onValueChange = { newEmail -> email = newEmail },
                                label = { Text("Email") },
                                shape = RoundedCornerShape(15.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(50.dp))

                            Text(
                                text = "Password",
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = Iceland,
                                color = Color.Black,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(horizontal = 35.dp)
                            )

                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 30.dp)
                                    .heightIn(max = 56.dp),
                                value = password,
                                onValueChange = { newPassword -> password = newPassword },
                                label = { Text("Password") },
                                shape = RoundedCornerShape(15.dp),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    val image = if (passwordVisible)
                                        Icons.Filled.Visibility
                                    else Icons.Filled.VisibilityOff

                                    IconButton(onClick = {passwordVisible = !passwordVisible}) {
                                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                                    }

                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Olvidé mi contraseña",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = Iceland,
                                    shadow = Shadow(
                                        color = Color.Black,
                                        offset = Offset(5f, 2f),
                                        blurRadius = 10f,
                                    )
                                ),
                                color = LoginColor,
                                fontSize = 25.sp,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(horizontal = 35.dp)

                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(20.dp),
                                onClick = {  authVM.login(email, password) },
                                shape = RoundedCornerShape(30.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LoginColor),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text("ENTRAR", color = Color.Black, fontFamily = Iceland, fontSize = 20.sp)
                            }

                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(20.dp),
                                onClick = { navigateToRegistro() },
                                shape = RoundedCornerShape(30.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LoginColor),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text("REGISTRARSE", color = Color.Black, fontFamily = Iceland, fontSize = 20.sp)
                            }
                        }

                        }

                    }
                }
            }
        )
    }
}


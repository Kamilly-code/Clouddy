package com.clouddy.application.ui.screen.login.screen


import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clouddy.application.R
import com.clouddy.application.domain.usecase.AuthState
import com.example.clouddy.ui.theme.ClouddyTheme
import com.example.clouddy.ui.theme.HoltwoodOneSC
import com.example.clouddy.ui.theme.Iceland
import com.example.clouddy.ui.theme.colorClouddy_1
import com.example.clouddy.ui.theme.colorClouddy_2
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import com.clouddy.application.ui.screen.login.components.CloudImageWithShadow
import com.clouddy.application.ui.screen.login.viewModel.AuthVM


@Composable
fun RegistroScreen( navigateHome: () -> Unit, navigateToLogin: () -> Unit) {
    ClouddyTheme {
        val authVM: AuthVM = hiltViewModel()
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var repeatPassword by remember { mutableStateOf("") }

        var passwordVisible by remember { mutableStateOf(false) }
        var repeatPasswordVisible by remember { mutableStateOf(false) }
        val isPasswordValid = remember(password) {
            password.matches(Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$"))
        }
        val passwordBorderColor by animateColorAsState(
            targetValue = when {
                password.isBlank() -> Color.Gray
                isPasswordValid -> Color(0xFF4CAF50)
                else -> Color(0xFFF44336)
            },
            animationSpec = tween(durationMillis = 500)
        )
        val isRepeatPasswordValid = remember(repeatPassword, password) {
            repeatPassword.matches(Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")) && repeatPassword == password
        }
        val repeatPasswordBorderColor by animateColorAsState(
            targetValue = when {
                repeatPassword.isBlank() -> Color.Gray
                isRepeatPasswordValid -> Color(0xFF4CAF50)
                else -> Color(0xFFF44336)
            },
            animationSpec = tween(durationMillis = 500)
        )

        var genero by remember { mutableStateOf("") }

        val authState = authVM.authState.observeAsState()
        val context = LocalContext.current

        LaunchedEffect(authState.value) {
            println("AuthState changed: ${authState.value}") // Log para depuração
            when (authState.value) {
                is AuthState.Authenticated -> {
                    println("Navigating to home...") // Log para depuração
                    navigateHome()
                }
                is AuthState.Error -> {
                    val error = (authState.value as AuthState.Error).message
                    println("Error: $error") // Log para depuração
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
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
                                    Color(0xFF5746BA),
                                    Color(0xFF729EE2),
                                    Color(0xFF43B8EE),
                                    Color(0xFF4CABF1),
                                    Color(0xFF1382F2)
                                )
                            )
                        )
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()

                    ) {
                        CloudImageWithShadow(imageRes = R.drawable.img1, modifier = Modifier.offset(x = (-100).dp, y = (-150).dp))
                        CloudImageWithShadow(imageRes = R.drawable.img2, modifier = Modifier.offset(x = 100.dp, y = (-55).dp))
                        CloudImageWithShadow(imageRes = R.drawable.img1, modifier = Modifier.offset(x = (-100).dp, y = 65.dp))
                        CloudImageWithShadow(imageRes = R.drawable.img2, modifier = Modifier.offset(x = 110.dp, y = 165.dp))
                        CloudImageWithShadow(imageRes = R.drawable.img1, modifier = Modifier.offset(x = (-110).dp, y = 285.dp))
                        CloudImageWithShadow(imageRes = R.drawable.img2, modifier = Modifier.offset(x = 90.dp, y = 395.dp))
                        CloudImageWithShadow(imageRes = R.drawable.img1, modifier = Modifier.offset(x = (-80).dp, y = 515.dp))
                        CloudImageWithShadow(imageRes = R.drawable.img2, modifier = Modifier.offset(x = 105.dp, y = 600.dp))

                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF074E8C).copy(alpha = 0.2f),
                                        Color(0xFF0A0A0D).copy(alpha = 0.2f),
                                    )
                                )

                            )) {


                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "REGISTRO",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = HoltwoodOneSC,
                                        fontSize = 25.sp
                                    ),
                                    color = colorClouddy_1
                                )
                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Name",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black,
                                    fontFamily = Iceland,
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .padding(horizontal = 35.dp)
                                )

                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 30.dp)
                                        .heightIn(max = 64.dp),
                                    value = name,
                                    onValueChange = { newName -> name = newName },
                                    label = { Text("Name") },
                                    shape = RoundedCornerShape(15.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedIndicatorColor = repeatPasswordBorderColor,
                                        unfocusedIndicatorColor = repeatPasswordBorderColor
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))



                                Text(
                                    text = "Email",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black,
                                    fontFamily = Iceland,
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .padding(horizontal = 35.dp)
                                )

                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 30.dp)
                                        .heightIn(max = 64.dp),
                                    value = email,
                                    onValueChange = { newEmail -> email = newEmail },
                                    label = { Text("Email") },
                                    shape = RoundedCornerShape(15.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedIndicatorColor = repeatPasswordBorderColor,
                                        unfocusedIndicatorColor = repeatPasswordBorderColor
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Password",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black,
                                    fontFamily = Iceland,
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .padding(horizontal = 35.dp)
                                )

                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 30.dp)
                                        .heightIn(max = 64.dp),
                                    value = password,
                                    onValueChange = { newPassword -> password = newPassword },
                                    label = { Text("Password") },
                                    shape = RoundedCornerShape(15.dp),
                                    singleLine = true,
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                            Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                                        }
                                    },

                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedIndicatorColor = passwordBorderColor,
                                        unfocusedIndicatorColor = passwordBorderColor
                                    )
                                )

                                if (password.isNotBlank()) {
                                    Text(
                                        text = if (isPasswordValid) "La contraseña es ser valida"
                                        else "La contraseña debe tener al menos 8 caracteres, 1 mayúscula y 1 número.",
                                        color = if (isPasswordValid) Color(0xFF4CAF50) else Color(0xFFF44336),
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .padding(start = 35.dp, top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))


                                Text(
                                    text = "Repeat Password",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black,
                                    fontFamily = Iceland,
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .padding(horizontal = 35.dp)
                                )

                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 30.dp)
                                        .heightIn(max = 64.dp),
                                    value = repeatPassword,
                                    onValueChange = { newRepeatPassword -> repeatPassword = newRepeatPassword },
                                    label = { Text("Repeat Password") },
                                    shape = RoundedCornerShape(15.dp),
                                    singleLine = true,
                                    visualTransformation = if (repeatPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        val image = if (repeatPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                        IconButton(onClick = { repeatPasswordVisible = !repeatPasswordVisible }) {
                                            Icon(imageVector = image, contentDescription = if (repeatPasswordVisible) "Hide password" else "Show password")
                                        }
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedIndicatorColor = repeatPasswordBorderColor,
                                        unfocusedIndicatorColor = repeatPasswordBorderColor
                                    )
                                )

                                if (repeatPassword.isNotBlank()) {
                                    Text(
                                        text = when {
                                            repeatPassword != password -> "Las contraseñas no coinciden"
                                            !repeatPassword.matches(Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")) -> "La contraseña debe tener al menos 8 caracteres, 1 mayúscula y 1 número."
                                            else -> "Las contraseñas coinciden"
                                        },
                                        color = if (isRepeatPasswordValid) Color(0xFF4CAF50) else Color(0xFFF44336),
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .padding(start = 35.dp, top = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Género",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black,
                                    fontFamily = Iceland,
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .padding(horizontal = 35.dp)
                                )


                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 30.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = genero == "Femenino",
                                            onClick = { genero = "Femenino" }
                                        )
                                        Text(text = "Femenino")
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = genero == "Masculino",
                                            onClick = { genero = "Masculino" }
                                        )
                                        Text(text = "Masculino")
                                    }
                                }



                                Spacer(modifier = Modifier.height(20.dp))

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
                                        onClick = { navigateToLogin() },
                                        shape = RoundedCornerShape(30.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = colorClouddy_1),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                        contentPadding = PaddingValues(16.dp),
                                    ) {
                                        Text("Cancel", color = Color.White)
                                    }
                                    Button(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(20.dp),
                                        onClick = {
                                            authVM.register(email, password, repeatPassword, name, genero) },
                                        shape = RoundedCornerShape(30.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = colorClouddy_2),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                        contentPadding = PaddingValues(16.dp)
                                    ) {
                                        Text("Crear", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
            }
    })
}
}


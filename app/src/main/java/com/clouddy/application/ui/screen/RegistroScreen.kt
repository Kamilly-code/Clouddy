package com.clouddy.application.ui.screen


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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clouddy.application.R
import com.example.clouddy.ui.theme.ClouddyTheme
import com.example.clouddy.ui.theme.HoltwoodOneSC
import com.example.clouddy.ui.theme.Iceland
import com.example.clouddy.ui.theme.colorClouddy_1
import com.example.clouddy.ui.theme.colorClouddy_2


@Composable
fun RegistroScreen( navigateLogin: () -> Unit) {
    ClouddyTheme {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var repeatPassword by remember { mutableStateOf("") }

        Scaffold(
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize() // Asegura que el Box ocupe todo el espacio disponible
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

                                TextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 30.dp)
                                        .heightIn(max = 56.dp),
                                    value = name,
                                    onValueChange = { newName -> name = newName },
                                    label = { Text("Name") },
                                    shape = RoundedCornerShape(15.dp)
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

                                TextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 30.dp)
                                        .heightIn(max = 56.dp),
                                    value = password,
                                    onValueChange = { newPassword -> password = newPassword },
                                    label = { Text("Password") },
                                    shape = RoundedCornerShape(15.dp)
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

                                TextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 30.dp)
                                        .heightIn(max = 56.dp),
                                    value = email,
                                    onValueChange = { newEmail -> email = newEmail },
                                    label = { Text("Email") },
                                    shape = RoundedCornerShape(15.dp)
                                )
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

                                TextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 30.dp)
                                        .heightIn(max = 56.dp),
                                    value = repeatPassword,
                                    onValueChange = { newRepeatPassword -> repeatPassword = newRepeatPassword },
                                    label = { Text("Repeat Password") },
                                    shape = RoundedCornerShape(15.dp)
                                )
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
                                        onClick = {  },
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
                                        onClick = { navigateLogin() },
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
            }
        )
    }
}


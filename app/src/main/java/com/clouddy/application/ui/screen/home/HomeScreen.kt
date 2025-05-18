package com.clouddy.application.ui.screen.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.clouddy.application.R
import com.clouddy.application.domain.usecase.AuthState
import com.clouddy.application.ui.screen.login.viewModel.AuthVM
import com.example.clouddy.ui.theme.ClouddyTheme

import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun HomeScreen(
    navigateToLogin: () -> Unit,
    navigateToNotes: () -> Unit,
    navigateToTask: () -> Unit,
    navigateToPomodoro: () -> Unit,
    navigateToCalendar: () -> Unit
) {
    val authVM: AuthVM = hiltViewModel()
    val authState = authVM.authState.observeAsState()

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navigateToLogin()
        }
    }

    val iconItems: List<Pair<ImageVector, Int>> = listOf(
        Icons.Filled.StickyNote2 to 0,
        Icons.Filled.AccessTime to 1,
        Icons.Filled.Check to 2,
        Icons.Filled.CalendarToday to 3
    )

    val cards = listOf("Notes", "Pomodoro", "Tasks", "Calendar")
    var selectedIndex by remember { mutableStateOf(0) }

    val rotation by animateFloatAsState(
        targetValue = selectedIndex * 180f,
        animationSpec = tween(600)
    )

    ClouddyTheme {
        Scaffold { padding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .drawBehind {
                        val dotColor = Color(0xFFE0E0E0)
                        val spacing = 40f
                        val dotRadius = 3f
                        val columns = (size.width / spacing).toInt()
                        val rows = (size.height / spacing).toInt()

                        for (row in 0..rows) {
                            for (column in 0..columns) {
                                drawCircle(
                                    color = dotColor,
                                    radius = dotRadius,
                                    center = Offset(
                                        x = column * spacing,
                                        y = row * spacing
                                    )
                                )
                            }
                        }
                    }
            ) {
                // Ícones laterais
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(60.dp)
                        .padding(top = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    iconItems.forEach { (icon, index) ->
                        IconButton(onClick = { selectedIndex = index }) {
                            Icon(imageVector = icon, contentDescription = null)
                        }
                    }
                }

                // Stack de cards com efeito de rotação
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    cards.forEachIndexed { index, label ->
                        val isVisible = index == selectedIndex
                        val offset = (index - selectedIndex) * 10

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(200.dp)
                                .graphicsLayer {
                                    rotationY = if (isVisible) 0f else 15f
                                    translationY = offset * 10f
                                    alpha = if (isVisible) 1f else 0.3f
                                    scaleX = if (isVisible) 1f else 0.95f
                                    scaleY = if (isVisible) 1f else 0.95f
                                    shadowElevation = if (isVisible) 16f else 8f
                                    cameraDistance = 16f * density
                                }
                                .shadow(
                                    elevation = if (isVisible) 16.dp else 4.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = Color.Black.copy(alpha = 0.2f),
                                    spotColor = Color.Black.copy(alpha = 0.3f)
                                )
                                .background(
                                    Color.White,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                                .clickable {
                                    when (index) {
                                        0 -> navigateToNotes()
                                        1 -> navigateToPomodoro()
                                        2 -> navigateToTask()
                                        3 -> navigateToCalendar()
                                    }
                                }
                                .zIndex(if (isVisible) 1f else 0f)
                        ) {
                            Column {
                                Text(text = label, style = MaterialTheme.typography.headlineSmall)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Clique para acessar $label", color = Color.Gray)
                            }
                        }
                    }

                    // Sair
                    IconButton(
                        onClick = { authVM.signOut() },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sign out")
                    }
                }
            }
        }
    }
}




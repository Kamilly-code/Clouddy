package com.clouddy.application.ui.screen.calendar.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DrawerDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun FullScreenCalendar(
    navigateToDayScreen: () -> Unit
) {
    val selectedDate = remember { mutableStateOf<LocalDate?>(null) }
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }

    val firstDayOfMonth = currentMonth.value.atDay(1)
    val startOffset = firstDayOfMonth.dayOfWeek.value % 7 // Segunda = 1, Domingo = 7

    val daysInCurrentMonth = currentMonth.value.lengthOfMonth()
    val previousMonth = currentMonth.value.minusMonths(1)
    val daysInPreviousMonth = previousMonth.lengthOfMonth()

    val totalCells = 42 // 6 semanas fixas
    val previousMonthDays = (daysInPreviousMonth - startOffset + 1..daysInPreviousMonth).toList()
    val currentMonthDays = (1..daysInCurrentMonth).toList()
    val remainingCells = totalCells - previousMonthDays.size - currentMonthDays.size
    val nextMonthDays = (1..remainingCells).toList()

    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2677B0))
                    .padding(vertical = 10.dp)
            ) {
                Column {
                    // Header (month navigation)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            currentMonth.value = currentMonth.value.minusMonths(1)
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Anterior", tint = Color.White)
                        }

                        Text(
                            text = currentMonth.value.format(formatter),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        IconButton(onClick = {
                            currentMonth.value = currentMonth.value.plusMonths(1)
                        }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Próximo", tint = Color.White)
                        }
                    }

                    // Weekday headers
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val weekDays = listOf(
                            DayOfWeek.MONDAY,
                            DayOfWeek.TUESDAY,
                            DayOfWeek.WEDNESDAY,
                            DayOfWeek.THURSDAY,
                            DayOfWeek.FRIDAY,
                            DayOfWeek.SATURDAY,
                            DayOfWeek.SUNDAY
                        )
                        weekDays.forEach { day ->
                            Text(
                                text = day.getDisplayName(TextStyle.SHORT, Locale("es", "ES")),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // Calendar grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(previousMonthDays.size) { index ->
                val day = previousMonthDays[index]
                val date = previousMonth.atDay(day)
                CalendarDayCell(date, isCurrentMonth = false, selectedDate.value == date) {
                    selectedDate.value = date
                    navigateToDayScreen()
                }
            }

            items(currentMonthDays.size) { index ->
                val day = currentMonthDays[index]
                val date = currentMonth.value.atDay(day)
                CalendarDayCell(date, isCurrentMonth = true, selectedDate.value == date) {
                    selectedDate.value = date
                    navigateToDayScreen()
                }
            }

            items(nextMonthDays.size) { index ->
                val day = nextMonthDays[index]
                val date = currentMonth.value.plusMonths(1).atDay(day)
                CalendarDayCell(date, isCurrentMonth = false, selectedDate.value == date) {
                    selectedDate.value = date
                    navigateToDayScreen()
                }
            }
        }
    }
}



@Composable
fun CalendarDayCell(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isToday = date == LocalDate.now()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.46f)
            .padding(2.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        backgroundColor = when {
            isSelected -> Color(0xFF48A9F3)
            isToday -> Color(0xFF2677B0)
            else -> Color.White
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    color = when {
                        isToday -> Color.White
                        isSelected -> Color.White
                        !isCurrentMonth -> Color.Gray
                        else -> Color.Black
                    }
                )
                if (isCurrentMonth) {
                    Text(
                        text = "Evento",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isToday) Color.White else Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
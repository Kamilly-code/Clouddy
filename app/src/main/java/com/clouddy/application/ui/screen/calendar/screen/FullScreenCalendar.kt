package com.clouddy.application.ui.screen.calendar.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
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

    val daysInMonth = currentMonth.value.lengthOfMonth()
    val firstDayOfMonth = currentMonth.value.atDay(1)

    val startOffset = when (firstDayOfMonth.dayOfWeek) {
        DayOfWeek.MONDAY -> 0
        DayOfWeek.TUESDAY -> 1
        DayOfWeek.WEDNESDAY -> 2
        DayOfWeek.THURSDAY -> 3
        DayOfWeek.FRIDAY -> 4
        DayOfWeek.SATURDAY -> 5
        DayOfWeek.SUNDAY -> 6
    }

    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es","ES"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
    ) {

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
                Icon(Icons.Default.ArrowBack, contentDescription = "Anterior")
            }

            Text(
                text = currentMonth.value.format(formatter),
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(onClick = {
                currentMonth.value = currentMonth.value.plusMonths(1)
            }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Próximo")
            }
        }

        val weekDays = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            weekDays.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale("es","ES")),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(startOffset) {
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(4.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 4.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Gray))
                }

            }


            items(daysInMonth) { index ->
                val date = currentMonth.value.atDay(index + 1)
                val isSelected = date == selectedDate
                val isToday = date == LocalDate.now()

                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(4.dp)
                        .clickable { navigateToDayScreen() },
                    shape = RoundedCornerShape(8.dp),
                    elevation = 4.dp,
                    backgroundColor = if (isToday) Color.Cyan else Color.White
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(if (isSelected) Color.Blue else Color.Transparent),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = (index + 1).toString(),
                                color = if (isSelected) Color.White else Color.Black
                            )
                            Text(
                                text = "Evento",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
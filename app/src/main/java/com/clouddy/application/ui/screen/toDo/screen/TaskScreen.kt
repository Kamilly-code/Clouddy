package com.clouddy.application.ui.screen.toDo.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clouddy.application.R
import com.clouddy.application.data.network.local.entity.Task
import com.clouddy.application.data.network.local.mapper.toTask
import com.clouddy.application.ui.screen.notes.components.CloudFABImage
import com.clouddy.application.ui.screen.toDo.viewModel.TaskViewModel
import com.example.clouddy.ui.theme.ClouddyTheme
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import kotlin.text.format

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(navigateToNotesScreen: (() -> Unit)? = null){
    val viewModel: TaskViewModel = hiltViewModel()
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())


    val currentUserId by viewModel.currentUserId.collectAsState()

    // Verificar autenticação
    if (currentUserId == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }


    var showAddTaskSheet by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }

    var selectedDate by remember {
        mutableStateOf(viewModel.formatDate(LocalDate.now()))
    }
    var showDatePicker by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val dragOffset = remember { mutableStateOf(0f) } // Offset de arrastre
    val dragThreshold = 100f // Umbral de arrastre para activar la navegación
    val hasNavigated = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val backendOnline by viewModel.isBackendOnline.collectAsState()


    val dragState = rememberDraggableState { delta ->
        dragOffset.value += delta
    }



    LaunchedEffect(Unit) {
        viewModel.checkAndSyncBackend(userId = currentUserId!!)
        viewModel.syncAllTasks()
    }


    LaunchedEffect(dragOffset.value) {
        if (!hasNavigated.value && dragOffset.value > dragThreshold) {
            hasNavigated.value = true
            navigateToNotesScreen?.invoke()
        }
    }


    ClouddyTheme {
        Scaffold(
            floatingActionButton = {
                CloudFABImage(
                    onClick = { showAddTaskSheet = true },
                    modifier = Modifier
                        .padding(end = 16.dp, bottom = 16.dp)
                        .graphicsLayer {
                            clip = false
                        }
                )
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                    .fillMaxSize()
                    .draggable(
                        state = dragState,
                        orientation = Orientation.Horizontal,
                        onDragStarted = { dragOffset.value = 0f },
                        onDragStopped = { dragOffset.value = 0f; hasNavigated.value = false }
                    )
                ) {
                    // Imagem de fundo
                    Image(
                        painter = painterResource(id = R.drawable.plants3),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {

                        Text(
                            text = "ToDo",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                                .align(Alignment.CenterHorizontally)
                        )

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(tasks, key = { it.id ?: 0L }) { task ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Checkbox(
                                            checked = task.isCompleted,
                                            onCheckedChange = {
                                                currentUserId?.let { userId ->
                                                    viewModel.storeTaskCompletation(
                                                        task.toTask(
                                                            userId = userId
                                                        )
                                                    )
                                                }
                                            }
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = task.task,
                                            modifier = Modifier.weight(1f),
                                            maxLines = 1,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                            )
                                        )

                                        IconButton(onClick = {
                                            currentUserId?.let { userId ->
                                                viewModel.removeTask(
                                                    task.toTask(userId = userId),
                                                    context
                                                )
                                            }
                                        }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Eliminar tareas"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }


                    if (showAddTaskSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showAddTaskSheet = false },
                            sheetState = sheetState,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                            tonalElevation = 4.dp,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                TextField(
                                    value = newTaskText,
                                    onValueChange = { newTaskText = it },
                                    label = { Text("Nueva tarea") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { showDatePicker = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Text("Data: $selectedDate")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.Edit, contentDescription = "Selecionar data")
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        if (newTaskText.isNotBlank()) {
                                            val task = Task(
                                                id = null,
                                                remoteId = null,
                                                task = newTaskText,
                                                date = selectedDate, // Usando a data selecionada
                                                isCompleted = false,
                                                isSynced = false,
                                                isDeleted = false,
                                                isUpdated = false,
                                                userId = currentUserId ?: return@Button
                                            )
                                            viewModel.addTask(task)
                                            newTaskText = ""
                                            showAddTaskSheet = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Añadir")
                                }
                            }
                        }
                    }

                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                            )
                                .parse(selectedDate)?.time ?: System.currentTimeMillis()
                        )

                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        datePickerState.selectedDateMillis?.let { millis ->
                                            selectedDate =
                                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                                    .format(Date(millis))
                                        }
                                        showDatePicker = false
                                    }
                                ) {
                                    Text("OK")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                }
            })
    }
}
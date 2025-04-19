package com.clouddy.application.ui.screen.notes.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clouddy.application.data.local.entity.Note
import com.clouddy.application.ui.screen.notes.viewModel.NotesViewModel
import com.example.clouddy.ui.theme.ClouddyTheme
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.clouddy.application.R
import com.clouddy.application.data.local.mapper.toNoteItem
import com.clouddy.application.ui.screen.notes.components.CloudFABImage
import com.clouddy.application.ui.screen.notes.components.NoteItemView
import kotlin.collections.plusAssign
import kotlin.compareTo
import kotlin.text.get

@Composable
fun NotesListScreen(
    viewModel: NotesViewModel,
    onNoteClicked: (Note) -> Unit,
    onAddNewNote: () -> Unit,
    navigateToTaskScreen: (() -> Unit)? = null
) {
    val notes by viewModel.allNotes.observeAsState(emptyList())
    var query by remember { mutableStateOf("") }

    val filteredNotes = notes.filter {
        (it.title ?: "").contains(query, ignoreCase = true) || (it.note ?: "").contains(query, ignoreCase = true)
    }


    val dragOffset = remember { mutableStateOf(0f) }
    val dragThreshold = 100f
    val hasNavigated = remember { mutableStateOf(false) }

    val dragState = rememberDraggableState { delta ->
        dragOffset.value += delta
    }

    // Lógica de navegação
    LaunchedEffect(dragOffset.value) {
        if (!hasNavigated.value && dragOffset.value < -dragThreshold) {
            hasNavigated.value = true
            navigateToTaskScreen?.invoke()
        }
    }

    ClouddyTheme {
        Scaffold(
            floatingActionButton = {
                CloudFABImage(
                    onClick = onAddNewNote,
                    modifier = Modifier
                        .padding(end = 16.dp, bottom = 16.dp)
                        .graphicsLayer {
                            clip = false
                        }
                )
            },
            content = { padding ->
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
                    // Conteúdo da tela
                    Image(
                        painter = painterResource(id = R.drawable.plants3),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp)
                    ) {
                        TextField(
                            value = query,
                            onValueChange = { query = it },
                            label = { Text("Buscar...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.LightGray.copy(alpha = 0.8f),
                                unfocusedContainerColor = Color.LightGray.copy(alpha = 0.8f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )

                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalItemSpacing = 2.dp,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            items(filteredNotes.size) { index ->
                                val note = filteredNotes[index]
                                val noteItem = note.toNoteItem()
                                NoteItemView(noteItem, onClick = { onNoteClicked(note) })
                            }
                        }
                    }

                }
            }
        )
    }
}
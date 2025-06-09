package com.clouddy.application.ui.screen.notes

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.hilt.navigation.compose.hiltViewModel
import com.clouddy.application.data.network.local.entity.Note
import com.clouddy.application.ui.screen.notes.screen.AddNote
import com.clouddy.application.ui.screen.notes.screen.NotesListScreen
import com.clouddy.application.ui.screen.notes.viewModel.NotesViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotesApp(navigateToTask: () -> Unit) {
    val viewModel: NotesViewModel = hiltViewModel()
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var isAddingNote by remember { mutableStateOf(false) }
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val anchors = mapOf(0f to 0, 300f * density.density to 1)

    LaunchedEffect(Unit) {
        viewModel.currentUserId.value?.let { userId ->
            viewModel.loadNotes(userId)
        }
    }

    if (isAddingNote || selectedNote != null) {
        AddNote(
            viewModel = viewModel,
            noteToEdit = selectedNote,
            onNoteSaved = {
                isAddingNote = false
                selectedNote = null
            },
            onDeleteNote = {
                isAddingNote = false
                selectedNote = null
            }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                    orientation = Orientation.Horizontal
                )
        ) {

            LaunchedEffect(swipeableState.currentValue) {
                if (swipeableState.currentValue == 1) {
                    scope.launch {
                        navigateToTask()
                    }
                }
            }

            NotesListScreen(
                viewModel = viewModel,
                onNoteClicked = {
                    selectedNote = it
                },
                onAddNewNote = {
                    isAddingNote = true
                },
                navigateToTaskScreen = { navigateToTask() }
            )
        }
    }
}




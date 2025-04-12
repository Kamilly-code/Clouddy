package com.clouddy.application.ui.screen.notes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clouddy.application.data.local.entity.Note
import com.clouddy.application.ui.screen.notes.screen.AddNote
import com.clouddy.application.ui.screen.notes.screen.NotesListScreen
import com.clouddy.application.ui.screen.notes.viewModel.NotesViewModel


@Composable
fun NotesApp(viewModel: NotesViewModel = viewModel()) {
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var isAddingNote by remember { mutableStateOf(false) }

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
        NotesListScreen(
            viewModel = viewModel,
            onNoteClicked = {
                selectedNote = it
            },
            onAddNewNote = {
                isAddingNote = true
            }
        )
    }
}


package com.clouddy.application.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clouddy.application.NoteItem
import com.clouddy.application.database.entity.Note
import com.clouddy.application.viewModel.NotesViewModel
import com.example.clouddy.ui.theme.ClouddyTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNote(
    viewModel: NotesViewModel,
    noteToEdit: Note? = null,
    onNoteSaved: () -> Unit,
    onDeleteNote: (() -> Unit)? = null
) {
    ClouddyTheme {
        var title by remember { mutableStateOf(noteToEdit?.title ?: "") }
        var content by remember { mutableStateOf(noteToEdit?.note ?: "") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (noteToEdit == null) "Add Note" else "Edit Note") },
                    navigationIcon = {
                        IconButton(onClick = onNoteSaved) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (noteToEdit != null) {
                            IconButton(onClick = {
                                viewModel.delete(noteToEdit)
                                onDeleteNote?.invoke()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }

                        IconButton(onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                                val newNote = Note(
                                    id = noteToEdit?.id,
                                    title = title,
                                    note = content,
                                    date = currentDate
                                )

                                if (noteToEdit != null) {
                                    viewModel.update(newNote)
                                } else {
                                    viewModel.insert(newNote)
                                }
                                onNoteSaved()
                            }
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Nota") },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
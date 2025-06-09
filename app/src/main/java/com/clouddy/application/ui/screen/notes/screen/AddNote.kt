package com.clouddy.application.ui.screen.notes.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.clouddy.application.R
import com.clouddy.application.data.network.local.entity.Note
import com.clouddy.application.ui.screen.notes.components.LinedTextField
import com.clouddy.application.ui.screen.notes.viewModel.NotesViewModel
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
        val context = LocalContext.current

        val currentUserId by viewModel.currentUserId.collectAsState()

        // Bloqueia a tela caso o usuário ainda não esteja autenticado
        if (currentUserId == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@ClouddyTheme
        }

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
                                viewModel.deleteNote(noteToEdit,context)
                                onDeleteNote?.invoke()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }

                        IconButton(onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                val newNote = Note(
                                    id = noteToEdit?.id,
                                    remoteId = noteToEdit?.remoteId ?: "",
                                    title = title,
                                    note = content,
                                    date = currentDate,
                                    userId = currentUserId ?: throw IllegalStateException("User not authenticated")
                                )

                                if (noteToEdit != null) {
                                    viewModel.updateNote(newNote,context)
                                } else {
                                    viewModel.insertOrUpdateNote(newNote)
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
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.plants4),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.7f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    )


                }
            }
        }
    }
}
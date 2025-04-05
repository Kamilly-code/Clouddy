package com.clouddy.application.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.clouddy.application.NoteItem
import com.example.clouddy.ui.theme.ClouddyTheme
import com.example.clouddy.ui.theme.LoginColor
import androidx.compose.foundation.lazy.items
import com.clouddy.application.database.entity.Note


@Composable
fun SearchBar(
    notesList: List<NoteItem>,
    navigateToAddNote: () -> Unit,
    onNoteClicked: (NoteItem) -> Unit
) {
    ClouddyTheme {
        var query by remember { mutableStateOf("") }

        val filteredNotes = notesList.filter {
            it.title.contains(query, ignoreCase = true) || it.note.contains(query, ignoreCase = true)
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigateToAddNote() },
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(50.dp),
                    containerColor = LoginColor
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar")
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Buscar...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    LazyColumn {
                        items(filteredNotes) { note ->
                            NoteItemView(note = note, onClick = { onNoteClicked(note) })
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun NotesAppPreview() {
    NotesApp()
}





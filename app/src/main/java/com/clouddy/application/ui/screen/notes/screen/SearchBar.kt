package com.clouddy.application.ui.screen.notes.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clouddy.ui.theme.ClouddyTheme
import androidx.compose.foundation.lazy.items
import com.clouddy.application.data.local.entity.Note
import com.clouddy.application.data.local.mapper.toNoteItem
import com.clouddy.application.ui.screen.notes.components.NoteItemView


@Composable
fun SearchBar(
    notesList: List<Note>,
    navigateToAddNote: () -> Unit,
    onNoteClicked: (Note) -> Unit
) {
    ClouddyTheme {
        var query by remember { mutableStateOf("") }

        val filteredNotes = notesList.filter {
            it.title.orEmpty().contains(query, ignoreCase = true) ||
                    it.note.orEmpty().contains(query, ignoreCase = true)
        }

        Scaffold(
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
                            val noteItem = note.toNoteItem()
                            NoteItemView(noteItem = noteItem, onClick = { onNoteClicked(note) })
                        }
                    }
                }
            }
        )
    }
}






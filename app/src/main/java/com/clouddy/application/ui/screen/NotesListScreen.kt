package com.clouddy.application.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clouddy.application.database.entity.Note
import com.clouddy.application.viewModel.NotesViewModel
import com.example.clouddy.ui.theme.ClouddyTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import com.clouddy.application.mapper.toNoteItem
import com.example.clouddy.ui.theme.LoginColor

@Composable
fun NotesListScreen(
    viewModel: NotesViewModel,
    onNoteClicked: (Note) -> Unit,
    onAddNewNote: () -> Unit
) {
    val notes by viewModel.allNotes.observeAsState(emptyList())
    var query by remember { mutableStateOf("") }

    val filteredNotes = notes.filter {
        (it.title ?: "").contains(query, ignoreCase = true) || (it.note ?: "").contains(query, ignoreCase = true)
    }
    ClouddyTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = onAddNewNote, shape = RoundedCornerShape(50.dp), containerColor = LoginColor) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            },
            containerColor = LoginColor
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Buscar...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.LightGray,
                        unfocusedContainerColor = Color.LightGray,
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
}
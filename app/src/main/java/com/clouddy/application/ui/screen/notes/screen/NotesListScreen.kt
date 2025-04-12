package com.clouddy.application.ui.screen.notes.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.clouddy.application.R
import com.clouddy.application.data.local.mapper.toNoteItem
import com.clouddy.application.ui.screen.notes.components.CloudFABImage
import com.clouddy.application.ui.screen.notes.components.NoteItemView

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
            content = { padding ->
                Box(modifier = Modifier.fillMaxSize()) {

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

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        CloudFABImage(
                            onClick = onAddNewNote
                        )
                    }
                }
            }
        )
    }
}
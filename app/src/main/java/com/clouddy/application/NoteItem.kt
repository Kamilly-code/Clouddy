package com.clouddy.application

import com.clouddy.application.database.entity.Note

data class NoteItem(val title: String, val note: String, val date: String)

fun Note.toNoteItem(): NoteItem {
    return NoteItem(
        title = this.title ?: "",
        note = this.note ?: "",
        date = this.date ?: ""
    )
}

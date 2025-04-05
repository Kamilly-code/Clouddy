package com.clouddy.application.mapper


import com.clouddy.application.NoteItem
import com.clouddy.application.database.entity.Note

fun Note.toNoteItem(): NoteItem {
    return NoteItem(
        title = this.title.orEmpty(),
        note = this.note.orEmpty(),
        date = this.date.orEmpty()
    )
}

fun NoteItem.toNote(id: Int? = null): Note {
    return Note(
        id = id,
        title = this.title,
        note = this.note,
        date = this.date
    )
}
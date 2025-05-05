package com.clouddy.application.data.local.mapper


import com.clouddy.application.domain.model.NoteItem
import com.clouddy.application.data.local.entity.Note
import com.clouddy.application.data.network.remote.note.NoteRequestDto


fun Note.toNoteItem(): NoteItem {
    return NoteItem(
        id = this.id,
        title = this.title.orEmpty(),
        note = this.note.orEmpty(),
        date = this.date.orEmpty()
    )
}
fun NoteItem.toNote(): Note {
    return Note(
        id = this.id,
        title = this.title,
        note = this.note,
        date = this.date
    )
}

fun NoteItem.toNoteRequestDto(): NoteRequestDto {
    return NoteRequestDto(
        title = this.title,
        note = this.note
    )
}

package com.clouddy.application.data.network.local.mapper


import com.clouddy.application.domain.model.NoteItem
import com.clouddy.application.data.network.local.entity.Note
import com.clouddy.application.data.network.remote.note.NoteRequestDto


fun Note.toNoteItem(): NoteItem {
    return NoteItem(
        id = this.id,
        remoteId = this.remoteId,
        title = this.title.orEmpty(),
        note = this.note.orEmpty(),
        date = this.date.orEmpty(),
        isSynced = this.isSynced,
        isDeleted = this.isDeleted,
        isUpdated = this.isUpdated
    )
}

fun NoteItem.toNote(userId: String): Note {
    return Note(
        id = this.id,
        title = this.title,
        note = this.note,
        date = this.date,
        remoteId = this.remoteId,
        isSynced = false,
        isDeleted = false,
        isUpdated = false,
        userId = userId
    )
}

fun NoteItem.toNoteRequestDto(userId: String): NoteRequestDto {
    return NoteRequestDto(
        title = this.title,
        note = this.note,
        remoteId = this.remoteId ?: "",
        userId = userId
    )
}
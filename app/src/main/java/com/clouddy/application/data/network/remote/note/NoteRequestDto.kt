package com.clouddy.application.data.network.remote.note

data class NoteRequestDto(val title: String,
                          val note: String,
                          val remoteId: String,
                          val userId: String)


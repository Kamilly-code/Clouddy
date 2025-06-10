package com.clouddy.application.data.network.remote.note

import java.time.LocalDate

data class NoteRequestDto(val title: String,
                          val note: String,
                          val remoteId: String,
                          val userId: String,
                          val date: String)


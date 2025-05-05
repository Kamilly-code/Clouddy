package com.clouddy.application.domain.model

import java.time.LocalDate

data class NoteItem(val id: Long? = null, val title: String, val note: String, val date: String = LocalDate.now().toString())
package com.clouddy.application.domain.model

import java.time.LocalDate


data class NoteItem(val id: Long? = null,
                    val remoteId: String? = null,
                    val title: String,
                    val note: String,
                    val date: String = LocalDate.now().toString(),
                    val isSynced: Boolean = false,
                    val isDeleted: Boolean = false,
                    val isUpdated: Boolean = false,
                    val userId: String)


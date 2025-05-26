package com.clouddy.application.data.network.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val remoteId: String? = null,
    @ColumnInfo(name = "title") val title: String? = " ",
    @ColumnInfo(name = "note") val note: String? = " ",
    @ColumnInfo(name = "date") val date: String,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val isUpdated: Boolean = false
)

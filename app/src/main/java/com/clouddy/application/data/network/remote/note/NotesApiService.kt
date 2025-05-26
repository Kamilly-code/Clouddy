package com.clouddy.application.data.network.remote.note

import com.clouddy.application.domain.model.NoteItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotesApiService {
    @POST("/notes")
    suspend fun insertNote(@Body note: NoteRequestDto): Response<NoteItem>

    @PUT("notes/{id}")
    suspend fun updateNote(
        @Path("id") id: String,
        @Body noteRequestDTO: NoteRequestDto
    ): Response<NoteItem>

    @GET("/notes")
    suspend fun getAllNotes(): Response<List<NoteItem>>

    @DELETE("/notes/{id}")
    suspend fun deleteNote(@Path("id") id: String): Response<Void>

    @DELETE("/notes")
    suspend fun deleteAll(): Response<Void>

}
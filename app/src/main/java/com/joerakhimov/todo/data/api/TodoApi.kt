package com.joerakhimov.todo.data.api

import com.joerakhimov.todo.data.dto.TodoRequestDto
import com.joerakhimov.todo.data.dto.TodoResponseDto
import com.joerakhimov.todo.data.dto.TodoListResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TodoApi {

    @GET("list")
    suspend fun getTodoList(): TodoListResponseDto

    @GET("list/{id}")
    suspend fun getTodo(@Path ("id") id: String): TodoResponseDto

    @POST("list")
    suspend fun addTodo(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TodoRequestDto
    ): TodoResponseDto

    @PUT("list/{id}")
    suspend fun updateTodo(
        @Path ("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TodoRequestDto):
            TodoResponseDto

    @DELETE("list/{id}")
    suspend fun deleteTodo(
        @Path ("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int):
            TodoResponseDto

}
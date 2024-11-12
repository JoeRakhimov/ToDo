package com.joerakhimov.todo.data.api

import com.joerakhimov.todo.data.dto.TaskRequestDto
import com.joerakhimov.todo.data.dto.TaskResponseDto
import com.joerakhimov.todo.data.dto.TasksResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TodoApi {

    @GET("list")
    suspend fun getTasks(): TasksResponseDto

    @GET("list/{id}")
    suspend fun getTask(@Path ("id") id: String): TaskResponseDto

    @POST("list")
    suspend fun addTask(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TaskRequestDto
    ): TaskResponseDto

    @PUT("list/{id}")
    suspend fun updateTask(
        @Path ("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body task: TaskRequestDto):
            TaskResponseDto

    @DELETE("list/{id}")
    suspend fun deleteTask(
        @Path ("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int):
            TaskResponseDto

}
package com.joerakhimov.todo.data.api

import retrofit2.http.GET
import retrofit2.http.Path

interface TodoApi {

    @GET("list")
    suspend fun getTasks(): TasksResponseDto

    @GET("list/{id}")
    suspend fun getTask(@Path ("id") id: String): TaskResponseDto

}
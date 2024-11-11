package com.joerakhimov.todo.data

import retrofit2.http.GET

interface TodoApi {

    @GET("list")
    suspend fun getTasks(): TasksResponse

}
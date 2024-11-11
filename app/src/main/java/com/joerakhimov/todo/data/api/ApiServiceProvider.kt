package com.joerakhimov.todo.data.api

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import com.joerakhimov.todo.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceProvider {

    // Function to create and return TodoApi instance
    fun provideTodoApi(context: Context): TodoApi {
        val token = BuildConfig.API_TOKEN // Replace with actual token
        
        // 1. Create OkHttpClient with interceptor for adding authorization token
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Log request and response body, headers, etc.
            })
            .addInterceptor(AuthorizationInterceptor(token))
            .addInterceptor(ChuckerInterceptor(context))
            .build()

        // 2. Create Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)  // Replace with actual API URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 3. Create and return TodoApi instance
        return retrofit.create(TodoApi::class.java)
    }

}
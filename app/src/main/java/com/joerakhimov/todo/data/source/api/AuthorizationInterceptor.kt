package com.joerakhimov.todo.data.source.api

import com.joerakhimov.todo.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(private val token: String = BuildConfig.API_TOKEN) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Add the Authorization header to the request
        val requestWithAuth = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(requestWithAuth)
    }
}
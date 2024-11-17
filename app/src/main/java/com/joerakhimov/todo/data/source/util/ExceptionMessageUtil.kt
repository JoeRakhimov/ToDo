package com.joerakhimov.todo.data.source.util

import android.content.Context
import com.joerakhimov.todo.R
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class ExceptionMessageUtil @Inject constructor(@ApplicationContext private val context: Context) {

    fun getHumanReadableErrorMessage(throwable: Throwable): String {
        val errorMessage: String = when (throwable) {
            is IOException -> {
                context.getString(R.string.network_error)
            }
            is HttpException -> {
                context.getString(R.string.something_went_wrong)
            }
            is TimeoutException -> {
                context.getString(R.string.timeout_error)
            }
            else -> {
                context.getString(R.string.something_went_wrong)
            }
        }
        return errorMessage
    }

}
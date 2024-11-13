package com.joerakhimov.todo.ui.common

import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException

fun Throwable.getHumanReadableErrorMessage(): String {
    val errorMessage: String = when (this) {
        is IOException -> {
            "Произошла ошибка сети."
        }
        is HttpException -> {
            "Произошла ошибка HTTP с кодом статуса: ${code()}"
        }
        is TimeoutException -> {
            "Запрос превысил время ожидания. Пожалуйста, попробуйте снова."
        }
        else -> {
            "Что-то пошло не так" // Something went wrong
        }
    }
   return errorMessage
}
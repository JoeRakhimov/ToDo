package com.joerakhimov.todo.ui

sealed class ScreenState<out T> {
    object Loading : ScreenState<Nothing>()
    data class Success<out T>(val data: T) : ScreenState<T>()
    data class Error(val message: String) : ScreenState<Nothing>()
}
package com.joerakhimov.todo.ui.common

sealed class State<out T> {
    object Loading : State<Nothing>()
    data class Success<out T>(val data: T) : State<T>()
    data class Error(val message: String) : State<Nothing>()
}
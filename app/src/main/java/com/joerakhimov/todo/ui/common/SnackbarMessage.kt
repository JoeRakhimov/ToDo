package com.joerakhimov.todo.ui.common

sealed class SnackbarMessage {
    data class TextMessage(val message: String): SnackbarMessage()
    object ShowingCachedData: SnackbarMessage()
    object TaskDescriptionCannotBeEmpty: SnackbarMessage()
}
package com.joerakhimov.todo.navigation

sealed class Screen(val route: String) {
    object Tasks: Screen("tasks")
    object Task: Screen("task")
}
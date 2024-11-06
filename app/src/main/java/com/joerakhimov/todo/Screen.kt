package com.joerakhimov.todo

sealed class Screen(val route: String) {
    object Tasks: Screen("tasks")
    object Task: Screen("task")
}
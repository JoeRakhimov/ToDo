package com.joerakhimov.todo

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Tasks.route
    ) {
        composable(route = Screen.Tasks.route) {
            TasksScreen(onAddTaskButtonClick = {
                navController.navigate(Screen.Task.route)
            })
        }
        composable(route = Screen.Task.route) {
            TaskScreen({
                navController.popBackStack()
            }, {})
        }
    }
}
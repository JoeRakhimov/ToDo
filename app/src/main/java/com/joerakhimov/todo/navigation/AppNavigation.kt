package com.joerakhimov.todo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.joerakhimov.todo.task.TaskScreen
import com.joerakhimov.todo.tasks.TasksScreen

const val KEY_TASK_ID = "taskId"
const val DEFAULT_TASK_ID = ""

@Composable
fun AppNavigation(
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Tasks.route
    ) {
        // Route for tasks list
        composable(route = Screen.Tasks.route) {
            TasksScreen(
                onAddTaskButtonClick = {
                    // Navigate to add task screen (no ID needed)
                    navController.navigate(Screen.Task.route)
                },
                onTaskClick = { taskId ->
                    // Navigate to edit task screen with taskId
                    navController.navigate("${Screen.Task.route}/$taskId")
                }
            )
        }
        // Route for adding a new task
        composable(route = Screen.Task.route) {
            TaskScreen(
                taskId = DEFAULT_TASK_ID, // Null ID for new task
                onExit = { navController.popBackStack() },
                onSave = {  }
            )
        }
        // Route for editing an existing task
        composable(
            route = "${Screen.Task.route}/{$KEY_TASK_ID}",
            arguments = listOf(navArgument(KEY_TASK_ID) { defaultValue = DEFAULT_TASK_ID })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString(KEY_TASK_ID) ?: DEFAULT_TASK_ID
            TaskScreen(
                taskId = taskId,
                onExit = { navController.popBackStack() },
                onSave = {  }
            )
        }
    }
}
package com.joerakhimov.todo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.joerakhimov.todo.data.TodoItemsRepository
import com.joerakhimov.todo.task.TaskScreen
import com.joerakhimov.todo.tasks.TasksScreen

const val KEY_TASK_ID = "taskId"
const val DEFAULT_TASK_ID = ""

sealed class Screen(val route: String) {
    object Tasks : Screen("tasks")
    object Task : Screen("task/{$KEY_TASK_ID}")
}

@Composable
fun AppNavigation(
) {
    val navController = rememberNavController()
    val repository: TodoItemsRepository = remember { TodoItemsRepository() }
    NavHost(
        navController = navController,
        startDestination = Screen.Tasks.route
    ) {

        // Route for tasks list
        composable(route = Screen.Tasks.route) {
            TasksScreen(
                repository,
                onAddNewTaskButtonClick = {
                    navController.navigate(Screen.Task.route)
                },
                onTaskClick = { taskId ->
                    navController.navigate("${Screen.Task.route}/$taskId")
                }
            )
        }

        // Route for adding a new task
        composable(route = Screen.Task.route) {
            TaskScreen(
                repository,
                taskId = DEFAULT_TASK_ID, // Null ID for new task
                onExit = { navController.popBackStack() }
            )
        }

        // Route for editing an existing task
        composable(
            route = "${Screen.Task.route}/{$KEY_TASK_ID}",
            arguments = listOf(navArgument(KEY_TASK_ID) { defaultValue = DEFAULT_TASK_ID })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString(KEY_TASK_ID) ?: DEFAULT_TASK_ID
            TaskScreen(
                repository,
                taskId = taskId,
                onExit = { navController.popBackStack() }
            )
        }
    }
}
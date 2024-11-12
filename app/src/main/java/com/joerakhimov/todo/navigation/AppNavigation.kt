package com.joerakhimov.todo.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.joerakhimov.todo.data.api.ApiServiceProvider
import com.joerakhimov.todo.data.TodoItemsRepository
import com.joerakhimov.todo.task.TaskScreen
import com.joerakhimov.todo.tasks.TasksScreen

const val KEY_TASK_ID = "taskId"
const val DEFAULT_TASK_ID = ""

const val PREFERENCES_NAME = "TodoPreferences"

sealed class Screen(val route: String) {
    object Tasks : Screen("tasks")
    object Task : Screen("task/{$KEY_TASK_ID}")
}

@Composable
fun AppNavigation(context: Context) {
    val navController = rememberNavController()
    val repository: TodoItemsRepository = remember {
        val todoApi = ApiServiceProvider.provideTodoApi(context)
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        TodoItemsRepository(todoApi, preferences)
    }
    NavHost(
        navController = navController,
        startDestination = Screen.Tasks.route
    ) {

        // Route for tasks list
        composable(route = Screen.Tasks.route) {
            TasksScreen(
                navController = navController,
                repository = repository,
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
                repository = repository,
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
                taskId = taskId,
                repository = repository,
                onExit = { navController.popBackStack() }
            )
        }
    }
}
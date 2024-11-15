package com.joerakhimov.todo.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.joerakhimov.todo.data.repository.ConnectivityRepository
import com.joerakhimov.todo.data.source.api.ApiServiceProvider
import com.joerakhimov.todo.data.source.db.TodoDatabase
import com.joerakhimov.todo.data.repository.TodoItemsRepository
import com.joerakhimov.todo.data.source.util.ExceptionMessageUtil
import com.joerakhimov.todo.ui.screens.task.TaskScreen
import com.joerakhimov.todo.ui.screens.tasks.TasksScreen

const val KEY_TODO_ID = "todoId"
const val DEFAULT_TODO_ID = ""

const val PREFERENCES_NAME = "TodoPreferences"

sealed class Screen(val route: String) {
    object Tasks : Screen("tasks")
    object Task: Screen("task")
}

@Composable
fun AppNavigation(context: Context) {
    val navController = rememberNavController()
    val connectivityRepository = remember { ConnectivityRepository(context) }
    val repository: TodoItemsRepository = remember {
        val todoApi = ApiServiceProvider.provideTodoApi(context)
        val dao = TodoDatabase.getDatabase(context).todoItemDao()
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        TodoItemsRepository(todoApi, dao, connectivityRepository, preferences)
    }
    val exceptionMessageUtil = remember { ExceptionMessageUtil(context) }
    NavHost(
        navController = navController,
        startDestination = Screen.Tasks.route
    ) {
        // Route for task list
        composable(route = Screen.Tasks.route) {
            TasksScreen(
                navController = navController,
                repository = repository,
                connectivity = connectivityRepository,
                exceptionMessageUtil = exceptionMessageUtil,
                onAddNewTodoButtonClick = {
                    navController.navigate(Screen.Task.route)
                },
                onTodoClick = { todoId ->
                    navController.navigate("${Screen.Task.route}/$todoId")
                }
            )
        }

        // Route for adding a new task
        composable(route = Screen.Task.route) {
            TaskScreen(
                repository = repository,
                connectivity = connectivityRepository,
                exceptionMessageUtil = exceptionMessageUtil,
                onExit = { navController.popBackStack() }
            )
        }

        // Route for editing an existing task
        composable(
            route = "${Screen.Task.route}/{$KEY_TODO_ID}",
            arguments = listOf(navArgument(KEY_TODO_ID) { defaultValue = DEFAULT_TODO_ID })
        ) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getString(KEY_TODO_ID) ?: DEFAULT_TODO_ID
            TaskScreen(
                todoId = todoId,
                repository = repository,
                connectivity = connectivityRepository,
                onExit = { navController.popBackStack() }
            )
        }
    }
}
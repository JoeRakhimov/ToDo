package com.joerakhimov.todo.ui.tasks

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.joerakhimov.todo.ui.theme.ToDoTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.joerakhimov.todo.data.repository.ConnectivityRepository
import com.joerakhimov.todo.data.api.ApiServiceProvider
import com.joerakhimov.todo.data.db.TodoDatabase
import com.joerakhimov.todo.data.repository.TodoItemsRepository
import com.joerakhimov.todo.navigation.PREFERENCES_NAME
import com.joerakhimov.todo.navigation.Screen
import com.joerakhimov.todo.ui.common.State
import com.joerakhimov.todo.ui.common.ErrorView
import com.joerakhimov.todo.ui.common.ProgressView

@Composable
fun TasksScreen(
    navController: NavHostController = rememberNavController(),
    repository: TodoItemsRepository = TodoItemsRepository(
        ApiServiceProvider.provideTodoApi(LocalContext.current),
        TodoDatabase.getDatabase(LocalContext.current).todoItemDao(),
        ConnectivityRepository(LocalContext.current),
        LocalContext.current.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    ),
    viewModel: TasksViewModel = viewModel<TasksViewModel>(
        factory = TasksViewModelFactory(repository)
    ),
    onAddNewTodoButtonClick: () -> Unit = {},
    onTodoClick: (todoId: String) -> Unit = {}
) {

    val snackbarHostState = viewModel.snackbarHostState

    LaunchedEffect(navController.currentBackStackEntry) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentRoute == Screen.Tasks.route) {
            viewModel.updateTodoItems()
        }
    }

    val state = viewModel.state.collectAsState().value

    when (state) {
        is State.Loading -> {
            ProgressView()
        }

        is State.Success -> {
            TasksScreenContent(
                state.data,
                onAddNewTodoButtonClick,
                snackbarHostState,
                onTodoClick,
                viewModel
            )
        }

        is State.Error -> {
            ErrorView(state.message) {
                viewModel.fetchTodoItems()
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun TasksScreenPreview() {
    ToDoTheme(dynamicColor = false) {
        TasksScreen()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TasksScreenPreviewDark() {
    ToDoTheme(dynamicColor = false) {
        TasksScreen()
    }
}
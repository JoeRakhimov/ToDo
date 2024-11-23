package com.joerakhimov.todo.ui.screens.tasks

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joerakhimov.todo.ui.theme.ToDoTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.joerakhimov.todo.app.TodoApp
import com.joerakhimov.todo.ui.navigation.Screen
import com.joerakhimov.todo.ui.common.State
import com.joerakhimov.todo.ui.common.ErrorView
import com.joerakhimov.todo.ui.common.ProgressView

@Composable
fun TasksScreen(
    navController: NavHostController,
    onAddNewTodoButtonClick: () -> Unit = {},
    onTodoClick: (todoId: String) -> Unit = {},
    tasksComponent: TasksComponent =
        (LocalContext.current.applicationContext as TodoApp)
            .appComponent
            .providesTaskComponent()
) {

    val viewModelFactory = tasksComponent.provideTasksViewModelFactory()
    val viewModel: TasksViewModel = viewModel(factory = viewModelFactory)

    LaunchedEffect(navController.currentBackStackEntry) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentRoute == Screen.Tasks.route) {
            viewModel.updateTodoItems()
        }
    }

    val state = viewModel.state.collectAsStateWithLifecycle().value

    when (state) {
        is State.Loading -> {
            ProgressView()
        }

        is State.Success -> {
            TasksScreenContent(
                state.data,
                onAddNewTodoButtonClick,
                onTodoClick,
                viewModel
            )
        }

        is State.Error -> {
            ErrorView(state.message, state.remainingSecondsBeforeRetry) {
                viewModel.fetchTodoItems()
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun TasksScreenPreview() {
    ToDoTheme(dynamicColor = false) {
        TasksScreen(
            navController = rememberNavController()
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TasksScreenPreviewDark() {
    ToDoTheme(dynamicColor = false) {
        TasksScreen(
            navController = rememberNavController()
        )
    }
}
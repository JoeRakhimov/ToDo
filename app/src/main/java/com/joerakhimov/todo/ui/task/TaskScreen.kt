package com.joerakhimov.todo.ui.task

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joerakhimov.todo.navigation.DEFAULT_TODO_ID
import com.joerakhimov.todo.data.repository.ConnectivityRepository
import com.joerakhimov.todo.data.api.ApiServiceProvider
import com.joerakhimov.todo.data.db.TodoDatabase
import com.joerakhimov.todo.data.repository.TodoItemsRepository
import com.joerakhimov.todo.navigation.PREFERENCES_NAME
import com.joerakhimov.todo.ui.common.State
import com.joerakhimov.todo.ui.common.ErrorView
import com.joerakhimov.todo.ui.common.ProgressView
import com.joerakhimov.todo.ui.theme.ToDoTheme

sealed class TaskScreenMode {
    object NewTask : TaskScreenMode()
    object EditTask : TaskScreenMode()
}

@Composable
fun TaskScreen(
    todoId: String = DEFAULT_TODO_ID,
    repository: TodoItemsRepository = TodoItemsRepository(
        ApiServiceProvider.provideTodoApi(LocalContext.current),
        TodoDatabase.getDatabase(LocalContext.current).todoItemDao(),
        ConnectivityRepository(LocalContext.current),
        LocalContext.current.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    ),
    viewModel: TaskViewModel = viewModel<TaskViewModel>(
        factory = TaskViewModelFactory(
            repository,
            todoId
        )
    ),
    onExit: () -> Unit = {}
) {

    val state = viewModel.state.collectAsState().value

    when (state) {
        is State.Loading -> {
            ProgressView()
        }
        is State.Success -> {
            TaskScreenContent(todoId, state.data, viewModel, onExit)
        }
        is State.Error -> {
            ErrorView(state.message) {
                viewModel.fetchTodoItem()
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun TaskScreenPreview() {
    ToDoTheme(dynamicColor = false) {
        TaskScreen()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TaskScreenPreviewDark() {
    ToDoTheme(dynamicColor = false) {
        TaskScreen()
    }
}
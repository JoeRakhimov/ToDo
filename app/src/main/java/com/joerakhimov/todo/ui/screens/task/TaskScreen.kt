package com.joerakhimov.todo.ui.screens.task

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joerakhimov.todo.app.TodoApp
import com.joerakhimov.todo.ui.navigation.DEFAULT_TODO_ID
import com.joerakhimov.todo.ui.common.State
import com.joerakhimov.todo.ui.common.ErrorView
import com.joerakhimov.todo.ui.common.ProgressView
import com.joerakhimov.todo.ui.di.TaskComponent
import com.joerakhimov.todo.ui.theme.ToDoTheme

sealed class TaskScreenMode {
    object NewTask : TaskScreenMode()
    object EditTask : TaskScreenMode()
}

@Composable
fun TaskScreen(
    todoId: String = DEFAULT_TODO_ID,
    onExit: () -> Unit = {},
    taskComponent: TaskComponent =
        (LocalContext.current.applicationContext as TodoApp)
            .appComponent
            .provideTaskComponentFactory()
            .create(todoId)
) {

    val viewModelFactory = taskComponent.provideTaskViewModelFactory()
    val viewModel: TaskViewModel = viewModel(factory = viewModelFactory)
    val state = viewModel.todoItemState.collectAsState().value

    when (state) {
        is State.Loading -> {
            ProgressView()
        }
        is State.Success -> {
            TaskScreenContent(todoId, state.data, viewModel, onExit)
        }
        is State.Error -> {
            ErrorView(state.message, state.remainingSecondsBeforeRetry) {
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
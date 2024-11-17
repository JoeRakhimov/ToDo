package com.joerakhimov.todo.ui.screens.task

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.joerakhimov.todo.ui.navigation.DEFAULT_TODO_ID
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
    onExit: () -> Unit = {}
) {

    val viewModel: TaskViewModel = hiltViewModel()

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
package com.joerakhimov.todo.ui.task

import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.navigation.DEFAULT_TODO_ID
import com.joerakhimov.todo.data.model.TodoItem

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TaskScreenContent(
    todoId: String,
    todo: TodoItem,
    viewModel: TaskViewModel,
    onExit: () -> Unit
) {
    val screenMode =
        if (todoId == DEFAULT_TODO_ID) TaskScreenMode.NewTask else TaskScreenMode.EditTask
    val todoItemSaved by viewModel.operationOnTodoCompleted.collectAsState()

    val snackbarHostState = viewModel.snackbarHostState

    LaunchedEffect(todoItemSaved) {
        if (todoItemSaved) {
            onExit()
        }
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp) {
                TopAppBar(
                    title = {},
                    navigationIcon = { IconButton(onClick = onExit) { CloseIcon() } },
                    actions = {
                        SaveAction(todo, onSave = {
                            when (screenMode) {
                                is TaskScreenMode.NewTask -> {
                                    viewModel.addTodoItem(it)
                                }
                                is TaskScreenMode.EditTask -> {
                                    viewModel.updateTodoItem(it)
                                }
                            }
                        })
                    }
                )
            }
        },
        content = { padding ->
            TaskDetailsContent(
                todo = todo,
                screenMode = screenMode,
                onDescriptionChange = {
                    viewModel.updateTodoItemDescription(it)
                },
                onDeleteButtonClick = {
                    viewModel.deleteTodoItem(it)
                },
                padding = padding
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.imePadding()
    )
}
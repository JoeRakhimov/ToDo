package com.joerakhimov.todo.ui.task

import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.R
import com.joerakhimov.todo.ui.common.SnackbarMessage
import com.joerakhimov.todo.ui.navigation.DEFAULT_TODO_ID
import com.joerakhimov.todo.ui.model.TodoItem
import kotlinx.coroutines.launch

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

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

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
                onImportanceChange = { viewModel.updateTodoImportance(it) },
                onDeadlineDateChange = { viewModel.updateTodoItemDeadline(it) },
                onDeleteButtonClick = {
                    viewModel.deleteTodoItem(it)
                },
                padding = padding
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.imePadding()
    )

    val context = LocalContext.current
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { it ->
            scope.launch {
                val message = when(it){
                    is SnackbarMessage.TextMessage -> it.message
                    is SnackbarMessage.ShowingCachedData -> context.getString(R.string.showing_cached_data)
                    is SnackbarMessage.TaskDescriptionCannotBeEmpty -> context.getString(R.string.task_description_cannot_be_empty)
                }
                snackbarHostState.showSnackbar(message)
                viewModel.clearSnackbarMessage()
            }
        }
    }

}
package com.joerakhimov.todo.ui.screens.tasks

import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import com.joerakhimov.todo.R
import com.joerakhimov.todo.ui.common.SnackbarMessage
import com.joerakhimov.todo.ui.model.TodoItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreenContent(
    todoItems: List<TodoItem>,
    onAddNewTodoButtonClick: () -> Unit,
    onTodoClick: (todoId: String) -> Unit,
    viewModel: TasksViewModel
) {

    //    val snackbarHostState = viewModel.snackbarHostState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val topAppBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var areCompletedTodosAreShown by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .imePadding(),
        topBar = {
            TasksTopAppBar(
                topAppBarScrollBehavior,
                todoItems,
                areCompletedTodosAreShown,
                onToggleShowCompleted = {
                    areCompletedTodosAreShown = !areCompletedTodosAreShown
                })
        },
        floatingActionButton = { AddTodoButton(onClick = onAddNewTodoButtonClick) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        TaskList(
            todoList = todoItems,
            showCompletedTodoList = areCompletedTodosAreShown,
            onTodoClick = onTodoClick,
            paddingValues = paddingValues,
            onAddNewTodoButtonClick = onAddNewTodoButtonClick,
            onTodoCompletedChange = {
                viewModel.completeTodoItem(it)
            },
        )
    }

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
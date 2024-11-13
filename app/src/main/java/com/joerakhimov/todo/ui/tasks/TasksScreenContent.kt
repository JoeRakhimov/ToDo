package com.joerakhimov.todo.ui.tasks

import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.joerakhimov.todo.data.model.TodoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreenContent(
    todoItems: List<TodoItem>,
    onAddNewTodoButtonClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onTodoClick: (todoId: String) -> Unit,
    viewModel: TasksViewModel
) {

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
}
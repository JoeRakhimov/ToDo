package com.joerakhimov.todo.ui.tasks

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.R
import com.joerakhimov.todo.data.model.TodoItem
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
    connectivityRepository: ConnectivityRepository = ConnectivityRepository(LocalContext.current),
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksScreenContent(
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
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksTopAppBar(
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    todoItems: List<TodoItem>,
    showCompletedTasks: Boolean,
    onToggleShowCompleted: () -> Unit
) {

    val topAppBarExpanded = topAppBarScrollBehavior.state.collapsedFraction == 0f
    val topAppBarCollapsed = topAppBarScrollBehavior.state.collapsedFraction == 1f

    Surface(shadowElevation = if (topAppBarCollapsed) 4.dp else 0.dp) {
        LargeTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            title = {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.my_tasks),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge
                        )
                        if (topAppBarExpanded) {
                            Text(
                                stringResource(
                                    R.string.completed,
                                    todoItems.count { it.done }),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiary
                            )
                        }
                    }
                    if (topAppBarExpanded) {
                        IconButton(onClick = onToggleShowCompleted, Modifier.padding(end = 8.dp)) {
                            Icon(
                                imageVector = if (showCompletedTasks) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = stringResource(R.string.toggle_completed),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            },
            actions = {
                if (topAppBarCollapsed) {
                    IconButton(onClick = onToggleShowCompleted) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = stringResource(R.string.toggle_completed),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            scrollBehavior = topAppBarScrollBehavior
        )
    }
}

@Composable
fun AddTodoButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            tint = Color.White,
            contentDescription = stringResource(R.string.add_task),
        )
    }
}

@Composable
fun TaskList(
    todoList: List<TodoItem>,
    showCompletedTodoList: Boolean,
    onTodoClick: (todoId: String) -> Unit,
    paddingValues: PaddingValues,
    onAddNewTodoButtonClick: () -> Unit,
    onTodoCompletedChange: (todo: TodoItem) -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        LazyColumn {
            item { Spacer(modifier = Modifier.padding(top = 16.dp)) }

            val filteredItems =
                if (showCompletedTodoList) todoList else todoList.filter { !it.done }
            items(filteredItems) { item ->
                TodoItemView(
                    todoItem = item,
                    onClick = { onTodoClick(it) },
                    onCheckboxCheckedChange = onTodoCompletedChange
                )
            }

            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onAddNewTodoButtonClick() }
                        .padding(start = 56.dp, top = 14.dp, bottom = 24.dp)
                ) {
                    Text(
                        stringResource(R.string.new_),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
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
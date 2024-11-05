package com.joerakhimov.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.ui.theme.ToDoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(todoItems: List<TodoItem>) {

    var completedTasksIncluded by remember { mutableStateOf(false) }
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val topAppBarExpanded = scrollBehavior.state.collapsedFraction == 0f
    val topAppBarCollapsed = scrollBehavior.state.collapsedFraction == 1f
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
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
                                            todoItems.count { it.isCompleted }),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onTertiary
                                    )
                                }
                            }
                            if (topAppBarExpanded) {
                                IconButton(
                                    onClick = { completedTasksIncluded = !completedTasksIncluded },
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (completedTasksIncluded) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = "Localized description",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                    },
                    actions = {
                        if (topAppBarCollapsed) {
                            IconButton(onClick = {
                                completedTasksIncluded = !completedTasksIncluded
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Visibility,
                                    contentDescription = "Localized description",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Handle FAB click */ },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    tint = Color.White,
                    contentDescription = "Add",
                )
            }
        },
    ) { paddingValues ->
        Box(
            Modifier
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Card(
                shape = RoundedCornerShape(8.dp)
            ) {
                LazyColumn(
                    contentPadding = paddingValues
                ) {
                    val tasksToShow =
                        if (completedTasksIncluded) todoItems
                        else todoItems.filter { !it.isCompleted }
                    items(tasksToShow) { item ->
                        TodoItemView(todoItem = item)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCollapsingToolbarExample() {
    val todoItems = TodoItemsRepository().getTodoItems()
    ToDoTheme(dynamicColor = false) {
        TasksScreen(todoItems)
    }
}
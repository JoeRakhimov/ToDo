package com.joerakhimov.todo.ui.tasks

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.R
import com.joerakhimov.todo.ui.model.TodoItem

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
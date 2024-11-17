package com.joerakhimov.todo.ui.screens.task

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.joerakhimov.todo.ui.model.TodoItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.R

@Composable
fun DeleteSection(
    screenMode: TaskScreenMode,
    todo: TodoItem,
    onClick: (TodoItem) -> Unit
) {
    Row(
        Modifier
            .height(72.dp)
            .fillMaxWidth()
            .then(
                when (screenMode) {
                    is TaskScreenMode.NewTask -> Modifier
                    is TaskScreenMode.EditTask -> Modifier.clickable { onClick(todo) }
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.Delete,
            contentDescription = stringResource(R.string.delete),
            tint = when (screenMode) {
                is TaskScreenMode.NewTask -> MaterialTheme.colorScheme.onSurface
                is TaskScreenMode.EditTask -> MaterialTheme.colorScheme.error
            },
            modifier = Modifier
                .height(18.dp)
                .padding(end = 12.dp),
        )
        Text(
            stringResource(R.string.delete),
            color = when (screenMode) {
                is TaskScreenMode.NewTask -> MaterialTheme.colorScheme.onSurface
                is TaskScreenMode.EditTask -> MaterialTheme.colorScheme.error
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
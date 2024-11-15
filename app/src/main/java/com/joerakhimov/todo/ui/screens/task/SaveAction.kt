package com.joerakhimov.todo.ui.screens.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.R
import com.joerakhimov.todo.ui.model.TodoItem

@Composable
fun SaveAction(
    todo: TodoItem,
    onSave: (todo: TodoItem) -> Unit,
) {
    Text(
        stringResource(R.string.save).uppercase(),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .padding(16.dp)
            .clickable {
                onSave(todo)
            }
    )
}
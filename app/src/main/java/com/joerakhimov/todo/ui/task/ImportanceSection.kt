package com.joerakhimov.todo.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.R
import com.joerakhimov.todo.data.model.Importance
import com.joerakhimov.todo.data.model.TodoItem

@Composable
fun ImportanceSection(
    todo: TodoItem
) {

    val importanceMenuExpanded = remember { mutableStateOf(false) }

    Box {
        Column(
            Modifier
                .height(72.dp)
                .fillMaxWidth()
                .clickable { importanceMenuExpanded.value = true },
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                stringResource(R.string.importance),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                when (todo.importance) {
                    Importance.LOW -> stringResource(R.string.low)
                    Importance.BASIC -> stringResource(R.string.normal)
                    Importance.IMPORTANT -> stringResource(R.string.urgent)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }

        ImportanceDropdownMenu(
            expanded = importanceMenuExpanded.value,
            onDismissRequest = { importanceMenuExpanded.value = false },
            onImportanceSelected = {
                todo.importance = it
                importanceMenuExpanded.value = false
            }
        )
    }
}

@Composable
private fun ImportanceDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onImportanceSelected: (Importance) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        DropdownMenuItem(text = {
            Text(stringResource(R.string.normal), color = MaterialTheme.colorScheme.onPrimary)
        }, onClick = {
            onImportanceSelected(Importance.BASIC)
            onDismissRequest()
        })
        DropdownMenuItem(text = {
            Text(stringResource(R.string.low), color = MaterialTheme.colorScheme.onPrimary)
        }, onClick = {
            onImportanceSelected(Importance.LOW)
            onDismissRequest()
        })
        DropdownMenuItem(text = {
            Row {
                Text(
                    "!! ${stringResource(R.string.urgent)}",
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }, onClick = {
            onImportanceSelected(Importance.IMPORTANT)
            onDismissRequest()
        })
    }
}
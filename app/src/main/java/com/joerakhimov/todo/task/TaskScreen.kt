package com.joerakhimov.todo.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.navigation.DEFAULT_TASK_ID
import com.joerakhimov.todo.R
import com.joerakhimov.todo.data.Importance
import com.joerakhimov.todo.data.TodoItem
import com.joerakhimov.todo.data.TodoItemsRepository
import com.joerakhimov.todo.ui.theme.ToDoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

fun String?.isNewTaskId() = this == DEFAULT_TASK_ID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    repository: TodoItemsRepository,
    taskId: String,
    onExit: () -> Unit,
    onSave: () -> Unit
) {
    val task by remember {
        mutableStateOf(
            repository.getTodoItems().find { it.id == taskId } ?:
            TodoItem(
                id = UUID.randomUUID().toString(),
                text = "",
                importance = Importance.NORMAL,
                deadline = null,
                isCompleted = false,
                createdAt = Date(),
                modifiedAt = null
            )
        )
    }
//    var importance by remember { mutableStateOf(Importance.NORMAL) }
    var importanceMenuExpanded by remember { mutableStateOf(false) }
//    var deadlineDate by remember { mutableStateOf<LocalDate?>(null) }
    var deadlineEnabled by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // Top App Bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Exit",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    Text(
                        stringResource(R.string.save).uppercase(),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { }
                    )
                }
            )
        },
        content = { padding ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
//                var textFieldValue by remember { mutableStateOf("") }
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Adjust elevation as needed
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = task.text,
                        onValueChange = { task.text = it },
                        placeholder = { Text(stringResource(R.string.what_to_do)) },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
                            focusedPlaceholderColor = Color.Transparent,
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box {

                    Column(
                        Modifier
                            .height(72.dp)
                            .fillMaxWidth()
                            .clickable { importanceMenuExpanded = true },
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            stringResource(R.string.importance),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            when (task.importance) {
                                Importance.LOW -> stringResource(R.string.low)
                                Importance.NORMAL -> stringResource(R.string.normal)
                                Importance.URGENT -> stringResource(R.string.urgent)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }

                    DropdownMenu(
                        expanded = importanceMenuExpanded,
                        onDismissRequest = { importanceMenuExpanded = false },
                        Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(text = {
                            Text(
                                stringResource(R.string.normal),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }, onClick = {
                            task.importance = Importance.NORMAL
                            importanceMenuExpanded = false
                        })
                        DropdownMenuItem(text = {
                            Text(
                                stringResource(R.string.low),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }, onClick = {
                            task.importance = Importance.LOW
                            importanceMenuExpanded = false
                        })
                        DropdownMenuItem(text = {
                            Row {
                                Text(
                                    "!! ${stringResource(R.string.urgent)}",
                                    color = MaterialTheme.colorScheme.onError
                                )
                            }
                        }, onClick = {
                            task.importance = Importance.URGENT
                            importanceMenuExpanded = false
                        })
                    }
                }

                Divider(
                    color = MaterialTheme.colorScheme.onSurface,
                    thickness = 0.5.dp
                )

                Box {


                    Row(
                        Modifier
                            .height(72.dp)
                            .fillMaxWidth()
                            .clickable { showDatePickerDialog = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.finish_till),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            if (task.deadline != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    task.deadline?.let {
                                        SimpleDateFormat(
                                            "dd.MM.yyyy",
                                            Locale.getDefault()
                                        ).format(it)
                                    } ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Switch(
                            checked = deadlineEnabled,
                            onCheckedChange = {
                                deadlineEnabled = it
                                if (deadlineEnabled) {
                                    showDatePickerDialog = true
                                } else {
                                    task.deadline = null
                                }
                            },
                            colors = SwitchDefaults.colors(
                                uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                                uncheckedBorderColor = MaterialTheme.colorScheme.onSurface,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                            )
                        )
                    }

                    if (showDatePickerDialog) {
                        // Convert deadlineDate to milliseconds in UTC time zone
                        val initialDateInMillis = task.deadline?.time

                        // Initialize DatePickerState with the corrected initial date in milliseconds
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = initialDateInMillis
                        )

                        DatePickerDialog(
                            onDismissRequest = { },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                                        // Convert the selected milliseconds to LocalDate in UTC
                                        task.deadline = Date(selectedDateMillis)
                                        deadlineEnabled = true
                                    }
                                    showDatePickerDialog = false
                                }) {
                                    Text(stringResource(R.string.Done))
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDatePickerDialog = false
                                    deadlineEnabled = task.deadline != null
                                }) {
                                    Text(stringResource(R.string.Cancel))
                                }
                            },
                        ) {
                            DatePicker(
                                state = datePickerState,
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            )
                        }
                    }
                }

                Divider(
                    color = MaterialTheme.colorScheme.onSurface,
                    thickness = 0.5.dp
                )

                Row(
                    Modifier
                        .height(72.dp)
                        .fillMaxWidth()
                        .clickable {
                            if (!taskId.isNewTaskId()) {

                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val color = if (taskId.isNewTaskId()) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = color,
                        modifier = Modifier
                            .height(18.dp)
                            .padding(end = 12.dp),
                    )
                    Text(
                        stringResource(R.string.delete),
                        color = color,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun TaskScreenPreview() {
    ToDoTheme(dynamicColor = false) {
        TaskScreen(TodoItemsRepository(), DEFAULT_TASK_ID, {}, {})
    }
}
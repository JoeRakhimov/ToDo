package com.joerakhimov.todo.task

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.navigation.DEFAULT_TASK_ID
import com.joerakhimov.todo.R
import com.joerakhimov.todo.data.ApiServiceProvider
import com.joerakhimov.todo.data.Importance
import com.joerakhimov.todo.data.TodoItem
import com.joerakhimov.todo.data.TodoItemsRepository
import com.joerakhimov.todo.ui.theme.ToDoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

sealed class TaskScreenMode {
    object NewTask : TaskScreenMode()
    object EditTask : TaskScreenMode()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    repository: TodoItemsRepository,
    taskId: String,
    onExit: () -> Unit
) {
    var task by remember { mutableStateOf(getTask(repository, taskId)) }
    val taskMode =
        if (taskId == DEFAULT_TASK_ID) TaskScreenMode.NewTask else TaskScreenMode.EditTask
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp) {
                TopAppBar(
                    title = {},
                    navigationIcon = { IconButton(onClick = onExit) { CloseIcon() } },
                    actions = {
                        SaveAction(task, taskMode, repository, onExit, context)
                    }
                )
            }
        },
        content = { padding ->
            TaskDetailsContent(
                task = task,
                screenMode = taskMode,
                onDescriptionChange = { task = task.copy(text = it) },
                onDeleteButtonClick = {
                    repository.deleteTodoItem(task)
                    onExit()
                },
                padding = padding
            )
        }
    )
}


private fun getTask(repository: TodoItemsRepository, taskId: String): TodoItem {
//    return repository.getTodoItems().find { it.id == taskId } ?:
    return TodoItem(
        id = UUID.randomUUID().toString(),
        text = "",
        importance = Importance.NORMAL,
        deadline = null,
        isCompleted = false,
        createdAt = Date(),
        modifiedAt = null
    )
}

@Composable
private fun SaveAction(
    task: TodoItem,
    screenMode: TaskScreenMode,
    repository: TodoItemsRepository,
    onExit: () -> Unit,
    context: Context
) {
    Text(
        stringResource(R.string.save).uppercase(),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .padding(16.dp)
            .clickable {
                if (task.text.isEmpty()) {
                    Toast
                        .makeText(
                            context,
                            context.getString(R.string.task_description_cannot_be_empty),
                            Toast.LENGTH_SHORT
                        )
                        .show()
                    return@clickable
                }
                when (screenMode) {
                    is TaskScreenMode.NewTask -> repository.addTodoItem(task)
                    is TaskScreenMode.EditTask -> {
                        task.modifiedAt = Date()
                        repository.updateTodoItem(task)
                    }
                }
                onExit()
            }
    )
}

@Composable
private fun TaskDetailsContent(
    task: TodoItem,
    screenMode: TaskScreenMode,
    onDescriptionChange: (String) -> Unit,
    onDeleteButtonClick: () -> Unit,
    padding: PaddingValues
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        TaskDescriptionField(task, onValueChange = onDescriptionChange)

        ImportanceSection(task = task)

        SectionDivider()

        DeadlineSection(task = task)

        SectionDivider()

        DeleteSection(screenMode, onDeleteButtonClick)
    }
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDescriptionField(task: TodoItem, onValueChange: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = task.text,
            onValueChange = { onValueChange(it) },
            placeholder = { Text(stringResource(R.string.what_to_do)) },
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences
            ),
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
}

@Composable
private fun ImportanceSection(
    task: TodoItem
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
                when (task.importance) {
                    Importance.LOW -> stringResource(R.string.low)
                    Importance.NORMAL -> stringResource(R.string.normal)
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
                task.importance = it
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
            onImportanceSelected(Importance.NORMAL)
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

@Composable
private fun DeadlineSection(
    task: TodoItem
) {

    val deadlineEnabled = remember { mutableStateOf(task.deadline != null) }
    val showDatePickerDialog = remember { mutableStateOf(false) }

    Box {
        Row(
            Modifier
                .height(72.dp)
                .fillMaxWidth()
                .clickable { showDatePickerDialog.value = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.finish_till),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                task.deadline?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(it),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Switch(
                checked = deadlineEnabled.value,
                onCheckedChange = { isChecked ->
                    deadlineEnabled.value = isChecked
                    if (deadlineEnabled.value) {
                        showDatePickerDialog.value = true
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

        if (showDatePickerDialog.value) {
            DeadlineDatePickerDialog(task, deadlineEnabled, showDatePickerDialog)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeadlineDatePickerDialog(
    task: TodoItem,
    deadlineEnabled: MutableState<Boolean>,
    showDatePickerDialog: MutableState<Boolean>
) {

    val initialSelectedDateMillis = task.deadline?.time
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialSelectedDateMillis)

    DatePickerDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                    task.deadline = Date(selectedDateMillis)
                    deadlineEnabled.value = true
                }
                showDatePickerDialog.value = false
            }) {
                Text(stringResource(R.string.done))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                showDatePickerDialog.value = false
                deadlineEnabled.value = task.deadline != null
            }) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        )
    }
}

@Composable
private fun DeleteSection(
    screenMode: TaskScreenMode,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .height(72.dp)
            .fillMaxWidth()
            .then(
                when (screenMode) {
                    is TaskScreenMode.NewTask -> Modifier
                    is TaskScreenMode.EditTask -> Modifier.clickable { onClick() }
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.Delete,
            contentDescription = "Delete",
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

@Composable
private fun CloseIcon() {
    Icon(
        Icons.Default.Close,
        contentDescription = "Exit",
        tint = MaterialTheme.colorScheme.onPrimary
    )
}


@Preview(showBackground = true)
@Composable
fun TaskScreenPreview() {
    val context = LocalContext.current
    ToDoTheme(dynamicColor = false) {
        TaskScreen(TodoItemsRepository(ApiServiceProvider.provideTodoApi(context)), DEFAULT_TASK_ID, {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TaskScreenPreviewDark() {
    val context = LocalContext.current
    ToDoTheme(dynamicColor = false) {
        TaskScreen(TodoItemsRepository(ApiServiceProvider.provideTodoApi(context)), DEFAULT_TASK_ID, {})
    }
}
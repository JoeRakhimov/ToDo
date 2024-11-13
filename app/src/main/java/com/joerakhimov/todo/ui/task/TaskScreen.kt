package com.joerakhimov.todo.ui.task

import android.content.Context
import android.content.res.Configuration
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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joerakhimov.todo.navigation.DEFAULT_TODO_ID
import com.joerakhimov.todo.R
import com.joerakhimov.todo.data.repository.ConnectivityRepository
import com.joerakhimov.todo.data.api.ApiServiceProvider
import com.joerakhimov.todo.data.db.TodoDatabase
import com.joerakhimov.todo.data.model.Importance
import com.joerakhimov.todo.data.model.TodoItem
import com.joerakhimov.todo.data.repository.TodoItemsRepository
import com.joerakhimov.todo.navigation.PREFERENCES_NAME
import com.joerakhimov.todo.ui.common.State
import com.joerakhimov.todo.ui.common.ErrorView
import com.joerakhimov.todo.ui.common.ProgressView
import com.joerakhimov.todo.ui.theme.ToDoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class TaskScreenMode {
    object NewTask : TaskScreenMode()
    object EditTask : TaskScreenMode()
}

@Composable
fun TaskScreen(
    todoId: String = DEFAULT_TODO_ID,
    repository: TodoItemsRepository = TodoItemsRepository(
        ApiServiceProvider.provideTodoApi(LocalContext.current),
        TodoDatabase.getDatabase(LocalContext.current).todoItemDao(),
        ConnectivityRepository(LocalContext.current),
        LocalContext.current.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    ),
    viewModel: TaskViewModel = viewModel<TaskViewModel>(
        factory = TaskViewModelFactory(
            repository,
            todoId
        )
    ),
    onExit: () -> Unit = {}
) {

    val state = viewModel.state.collectAsState().value

    when (state) {
        is State.Loading -> {
            ProgressView()
        }
        is State.Success -> {
            TaskScreenContent(todoId, state.data, viewModel, onExit)
        }
        is State.Error -> {
            ErrorView(state.message) {
                viewModel.fetchTodoItem()
            }
        }
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TaskScreenContent(
    todoId: String,
    todo: TodoItem,
    viewModel: TaskViewModel,
    onExit: () -> Unit
) {
    val screenMode =
        if (todoId == DEFAULT_TODO_ID) TaskScreenMode.NewTask else TaskScreenMode.EditTask
    val todoItemSaved by viewModel.operationOnTodoCompleted.collectAsState()
    val context = LocalContext.current

    val snackbarHostState = viewModel.snackbarHostState

    LaunchedEffect(todoItemSaved) {
        if (todoItemSaved) {
            onExit()
        }
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp) {
                TopAppBar(
                    title = {},
                    navigationIcon = { IconButton(onClick = onExit) { CloseIcon() } },
                    actions = {
                        SaveAction(todo, onSave = {
                            when (screenMode) {
                                is TaskScreenMode.NewTask -> {
                                    viewModel.addTodoItem(it)
                                }
                                is TaskScreenMode.EditTask -> {
                                    viewModel.updateTodoItem(it)
                                }
                            }
                        })
                    }
                )
            }
        },
        content = { padding ->
            TaskDetailsContent(
                todo = todo,
                screenMode = screenMode,
                onDescriptionChange = {
                    viewModel.updateTodoItemDescription(it)
//                    todoItem = todoItem?.copy(text = it)
                },
                onDeleteButtonClick = {
                    viewModel.deleteTodoItem(it)
//                    onExit()
                },
                padding = padding
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.imePadding()
    )
}

@Composable
private fun SaveAction(
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

@Composable
private fun TaskDetailsContent(
    todo: TodoItem,
    screenMode: TaskScreenMode,
    onDescriptionChange: (String) -> Unit,
    onDeleteButtonClick: (TodoItem) -> Unit,
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
        TaskDescriptionField(todo, onValueChange = onDescriptionChange)

        ImportanceSection(todo = todo)

        SectionDivider()

        DeadlineSection(todo = todo)

        SectionDivider()

        DeleteSection(screenMode, todo, onDeleteButtonClick)
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
private fun TaskDescriptionField(todo: TodoItem, onValueChange: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = todo.text,
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

@Composable
private fun DeadlineSection(
    todo: TodoItem
) {

    val deadlineEnabled = remember { mutableStateOf(todo.deadline != null) }
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
                todo.deadline?.let {
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
                        todo.deadline = null
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
            DeadlineDatePickerDialog(todo, deadlineEnabled, showDatePickerDialog)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeadlineDatePickerDialog(
    todo: TodoItem,
    deadlineEnabled: MutableState<Boolean>,
    showDatePickerDialog: MutableState<Boolean>
) {

    val initialSelectedDateMillis = todo.deadline?.time
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = initialSelectedDateMillis)

    DatePickerDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                    todo.deadline = Date(selectedDateMillis)
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
                deadlineEnabled.value = todo.deadline != null
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

@Composable
private fun CloseIcon() {
    Icon(
        Icons.Default.Close,
        contentDescription = stringResource(R.string.close),
        tint = MaterialTheme.colorScheme.onPrimary
    )
}


@Preview(showBackground = true)
@Composable
fun TaskScreenPreview() {
    ToDoTheme(dynamicColor = false) {
        TaskScreen()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TaskScreenPreviewDark() {
    ToDoTheme(dynamicColor = false) {
        TaskScreen()
    }
}
package com.joerakhimov.todo.ui.screens.task

import androidx.compose.foundation.background
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joerakhimov.todo.R
import com.joerakhimov.todo.ui.model.TodoItem
import java.util.Date
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DeadlineSection(
    todo: TodoItem,
    onDeadlineDateChange: (Date?) -> Unit
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
                        onDeadlineDateChange(null)
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
            DeadlineDatePickerDialog(todo, deadlineEnabled, showDatePickerDialog, onDeadlineDateChange)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeadlineDatePickerDialog(
    todo: TodoItem,
    deadlineEnabled: MutableState<Boolean>,
    showDatePickerDialog: MutableState<Boolean>,
    onDeadlineDateChange: (Date?) -> Unit
) {

    val initialSelectedDateMillis = todo.deadline?.time
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = initialSelectedDateMillis)

    DatePickerDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                    onDeadlineDateChange(Date(selectedDateMillis))
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
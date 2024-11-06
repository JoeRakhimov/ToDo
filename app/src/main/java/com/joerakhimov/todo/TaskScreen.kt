package com.joerakhimov.todo

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import com.joerakhimov.todo.ui.theme.ToDoTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(onExit: () -> Unit, onSave: () -> Unit) {
    var importance by remember { mutableStateOf("Нет") }
    var expanded by remember { mutableStateOf(false) }
    var deadlineEnabled by remember { mutableStateOf(false) }
    var deadlineDate by remember { mutableStateOf<LocalDate?>(null) }
    var showImportanceDialog by remember { mutableStateOf(false) }
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
                        modifier = Modifier.padding(16.dp)
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
                var textFieldValue by remember { mutableStateOf("") }
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Adjust elevation as needed
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
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
                            .clickable { expanded = true },
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            stringResource(R.string.importance),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            importance,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(text = {
                            Text(
                                "Нет",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }, onClick = {
                            importance = "Нет"
                            expanded = false
                        })
                        DropdownMenuItem(text = {
                            Text(
                                "Низкий",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }, onClick = {
                            importance = "Низкий"
                            expanded = false
                        })
                        DropdownMenuItem(text = {
                            Row {
                                Text("!! Высокий", color = MaterialTheme.colorScheme.onError)
                            }
                        }, onClick = {
                            importance = "Высокий"
                            expanded = false
                        })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Deadline and Switch
                Text("Deadline", style = MaterialTheme.typography.bodyLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Enable Deadline", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = deadlineEnabled,
                        onCheckedChange = { isChecked ->
                            deadlineEnabled = isChecked
                            if (!isChecked) deadlineDate = null
                        }
                    )
                }

                // Deadline Calendar Dialog
                if (deadlineEnabled && showDatePickerDialog) {
                    DatePickerDialog(
                        initialDate = LocalDate.now(),
                        onDateSelected = { date ->
                            deadlineDate = date
                            showDatePickerDialog = false
                        },
                        onDismiss = { showDatePickerDialog = false }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Delete Button
                IconButton(
                    onClick = { /* Handle delete logic */ },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportanceDialog(onDismiss: () -> Unit, onImportanceSelected: (String) -> Unit) {
    val options = listOf("Urgent", "Normal", "Low")
    var selectedImportance by remember { mutableStateOf(options[1]) }

//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Select Importance") },
//        buttons = {
//            Column(modifier = Modifier.padding(16.dp)) {
//                options.forEach { importance ->
//                    TextButton(onClick = { onImportanceSelected(importance) }) {
//                        Text(importance)
//                    }
//                }
//            }
//        }
//    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePicker = remember { LocalDate.now() }

    // Assuming you have a date picker available here
    // Use an existing date picker composable or integrate with a library

//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Pick a Date") },
//        buttons = {
//            Column(modifier = Modifier.padding(16.dp)) {
//                // Add date picker UI here (use a date picker library or create your own)
//                TextButton(onClick = {
//                    onDateSelected(datePicker)
//                }) {
//                    Text("Set Date")
//                }
//            }
//        }
//    )
}


@Preview(showBackground = true)
@Composable
fun TaskScreenPreview() {
    ToDoTheme(dynamicColor = false) {
        TaskScreen({}, {})
    }
}
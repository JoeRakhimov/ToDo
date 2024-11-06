package com.joerakhimov.todo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.ui.theme.ToDoTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(onExit: () -> Unit, onSave: () -> Unit) {
    var importance by remember { mutableStateOf("Normal") }
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
                        Icon(Icons.Default.Close, contentDescription = "Exit")
                    }
                },
                actions = {
                    Text(stringResource(R.string.save))
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Multiline TextField with hint
                var textFieldValue by remember { mutableStateOf("") }
                TextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Importance Title with Subtitle
                Text("Importance", style = MaterialTheme.typography.headlineSmall)
                Text(importance, style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = { showImportanceDialog = true }) {
                    Text("Change Importance")
                }

                // Importance Dialog
                if (showImportanceDialog) {
                    ImportanceDialog(
                        onDismiss = { showImportanceDialog = false },
                        onImportanceSelected = { selectedImportance ->
                            importance = selectedImportance
                            showImportanceDialog = false
                        }
                    )
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
fun DatePickerDialog(initialDate: LocalDate, onDateSelected: (LocalDate) -> Unit, onDismiss: () -> Unit) {
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
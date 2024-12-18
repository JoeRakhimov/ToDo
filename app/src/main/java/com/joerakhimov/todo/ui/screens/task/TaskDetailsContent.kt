package com.joerakhimov.todo.ui.screens.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.ui.model.Importance
import com.joerakhimov.todo.ui.model.TodoItem
import java.util.Date


@Composable
fun TaskDetailsContent(
    todo: TodoItem,
    screenMode: TaskScreenMode,
    onDescriptionChange: (String) -> Unit,
    onImportanceChange: (Importance) -> Unit,
    onDeadlineDateChange: (Date?) -> Unit,
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

        ImportanceSection(todo = todo, onImportanceChange)

        SectionDivider()

        DeadlineSection(
            todo = todo,
            onDeadlineDateChange = onDeadlineDateChange
        )

        SectionDivider()

        DeleteSection(screenMode, todo, onDeleteButtonClick)
    }
}
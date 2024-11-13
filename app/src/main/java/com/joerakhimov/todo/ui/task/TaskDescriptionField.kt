package com.joerakhimov.todo.ui.task

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.R
import com.joerakhimov.todo.data.model.TodoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDescriptionField(todo: TodoItem, onValueChange: (String) -> Unit) {
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
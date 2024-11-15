package com.joerakhimov.todo.ui.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.R
import com.joerakhimov.todo.ui.model.TodoItem

@Composable
fun TaskList(
    todoList: List<TodoItem>,
    showCompletedTodoList: Boolean,
    onTodoClick: (todoId: String) -> Unit,
    paddingValues: PaddingValues,
    onAddNewTodoButtonClick: () -> Unit,
    onTodoCompletedChange: (todo: TodoItem) -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        LazyColumn {
            item { Spacer(modifier = Modifier.padding(top = 16.dp)) }

            val filteredItems =
                if (showCompletedTodoList) todoList else todoList.filter { !it.done }
            items(filteredItems) { item ->
                TodoItemView(
                    todoItem = item,
                    onClick = { onTodoClick(it) },
                    onCheckboxCheckedChange = onTodoCompletedChange
                )
            }

            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onAddNewTodoButtonClick() }
                        .padding(start = 56.dp, top = 14.dp, bottom = 24.dp)
                ) {
                    Text(
                        stringResource(R.string.new_),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    }
}
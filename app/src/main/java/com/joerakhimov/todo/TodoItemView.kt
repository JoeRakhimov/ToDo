package com.joerakhimov.todo

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Date

@Composable
fun TodoItemView(todoItem: TodoItem) {
    Row {
        Checkbox(
            checked = todoItem.isCompleted,
            onCheckedChange = {},
            colors = CheckboxDefaults.colors(
                uncheckedColor = if(todoItem.importance == Importance.URGENT) MaterialTheme.colorScheme.error else Color.Gray
            ),
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = todoItem.text,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .padding(vertical = 12.dp)
        )
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "Info icon",
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTodoItemView1() {
    val sampleTodoItem = TodoItem(
        "todo_1",
        "Buy grocery",
        Importance.NORMAL,
        null,
        false,
        Date(),
        null
    )
    TodoItemView(todoItem = sampleTodoItem)

}

@Preview(showBackground = true)
@Composable
fun PreviewTodoItemView2() {
    val sampleTodoItem2 = TodoItem(
        "todo_1",
        "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрезается текст когда текст слишком длинный",
        Importance.NORMAL,
        null,
        false,
        Date(),
        null
    )
    TodoItemView(todoItem = sampleTodoItem2)
}

@Preview(showBackground = true)
@Composable
fun PreviewTodoItemView3() {
    val sampleTodoItem2 = TodoItem(
        "todo_1",
        "Buy grocery",
        Importance.NORMAL,
        null,
        true,
        Date(),
        null
    )
    TodoItemView(todoItem = sampleTodoItem2)
}

@Preview(showBackground = true)
@Composable
fun PreviewTodoItemView4() {
    val sampleTodoItem2 = TodoItem(
        "todo_1",
        "Buy grocery",
        Importance.URGENT,
        null,
        false,
        Date(),
        null
    )
    TodoItemView(todoItem = sampleTodoItem2)
}


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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joerakhimov.todo.ui.theme.ToDoTheme
import java.util.Date

@Composable
fun TodoItemView(todoItem: TodoItem) {
    Row {
        Checkbox(
            checked = todoItem.isCompleted,
            onCheckedChange = {},
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.secondary,
                checkmarkColor = Color.White,
                uncheckedColor = when {
                    todoItem.importance == Importance.URGENT -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                },
            ),
            modifier = Modifier.padding(4.dp)
        )
        if(todoItem.importance == Importance.URGENT) {
            Text(text = "!!",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp, end = 6.dp)
            )
        } else if(todoItem.importance == Importance.LOW){
            Text(text = "↓",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp, end = 6.dp)
            )
        }
        Text(
            text = todoItem.text,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = if (todoItem.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None,
            color = if(todoItem.isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
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
    ToDoTheme(dynamicColor = false) {
        TodoItemView(todoItem = sampleTodoItem)
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewTodoItemView2() {
    val sampleTodoItem2 = TodoItem(
        "todo_2",
        "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрезается текст когда текст слишком длинный",
        Importance.NORMAL,
        null,
        false,
        Date(),
        null
    )
    ToDoTheme(dynamicColor = false) {
        TodoItemView(todoItem = sampleTodoItem2)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTodoItemView3() {
    val sampleTodoItem2 = TodoItem(
        "todo_3",
        "Купить что-то",
        Importance.LOW,
        null,
        false,
        Date(),
        null
    )
    ToDoTheme(dynamicColor = false) {
        TodoItemView(todoItem = sampleTodoItem2)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTodoItemView4() {
    val sampleTodoItem2 = TodoItem(
        "todo_4",
        "Купить что-то",
        Importance.URGENT,
        null,
        false,
        Date(),
        null
    )
    ToDoTheme(dynamicColor = false) {
        TodoItemView(todoItem = sampleTodoItem2)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTodoItemView5() {
    val sampleTodoItem2 = TodoItem(
        "todo_5",
        "Купить что-то",
        Importance.NORMAL,
        null,
        true,
        Date(),
        null
    )
    ToDoTheme(dynamicColor = false) {
        TodoItemView(todoItem = sampleTodoItem2)
    }
}


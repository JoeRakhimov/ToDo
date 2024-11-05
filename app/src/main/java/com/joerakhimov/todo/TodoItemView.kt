package com.joerakhimov.todo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.ui.theme.ToDoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            ).copy(
                uncheckedBoxColor =
                when {
                    todoItem.isCompleted -> MaterialTheme.colorScheme.secondary
                    todoItem.importance == Importance.URGENT -> MaterialTheme.colorScheme.error.copy(
                        alpha = 0.16f
                    )
                    else -> Color.Transparent
                }
            ),
            modifier = Modifier.padding(4.dp)
        )
        if (!todoItem.isCompleted) {
            if (todoItem.importance == Importance.URGENT) {
                Image(
                    painter = painterResource(id = R.drawable.urgent),
                    contentDescription = "Urgent icon",
                    modifier = Modifier.padding(top = 20.dp, end = 6.dp)
                )
            } else if (todoItem.importance == Importance.LOW) {
                Image(
                    painter = painterResource(id = R.drawable.low),
                    contentDescription = "Not important icon",
                    modifier = Modifier.padding(top = 20.dp, end = 6.dp)
                )
            }
        }
        Column(
            Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .padding(vertical = 14.dp)
        ) {
            Text(
                text = todoItem.text,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = if (todoItem.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None,
                color = if (todoItem.isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
            )
            if (!todoItem.isCompleted && todoItem.deadline != null) {
                Text(
                    text = SimpleDateFormat(
                        "dd.MM.yyyy HH:mm",
                        Locale.getDefault()
                    ).format(todoItem.deadline),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "Info icon",
            tint = MaterialTheme.colorScheme.onTertiary,
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

@Preview(showBackground = true)
@Composable
fun PreviewTodoItemView6() {
    val sampleTodoItem2 = TodoItem(
        "todo_6",
        "Купить что-то",
        Importance.NORMAL,
        Date(),
        false,
        Date(),
        null
    )
    ToDoTheme(dynamicColor = false) {
        TodoItemView(todoItem = sampleTodoItem2)
    }
}


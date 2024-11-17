package com.joerakhimov.todo.ui.screens.tasks

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.ui.model.Importance
import com.joerakhimov.todo.R
import com.joerakhimov.todo.ui.model.TodoItem
import com.joerakhimov.todo.ui.theme.ToDoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodoItemView(
    todoItem: TodoItem,
    onClick: (todoId: String) -> Unit = {},
    onCheckboxCheckedChange: (TodoItem) -> Unit = {}
) {
    val checkboxColors = getCheckboxColors(todoItem)
    val importanceIcon = getImportanceIcon(todoItem.importance)

    Row(
        Modifier
            .clickable { onClick(todoItem.id) }
            .padding(horizontal = 8.dp)
    ) {
        Checkbox(
            checked = todoItem.done,
            onCheckedChange = {
                onCheckboxCheckedChange(todoItem.copy(done = !todoItem.done))
            },
            colors = checkboxColors,
            modifier = Modifier.padding(4.dp)
        )

        importanceIcon?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = "${todoItem.importance} importance icon",
                modifier = Modifier
                    .padding(top = 20.dp, end = 6.dp)
            )
        }

        Column(
            Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = todoItem.text,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = if (todoItem.done) TextDecoration.LineThrough else TextDecoration.None,
                color = if (todoItem.done) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground
            )
            todoItem.deadline?.let { deadline ->
                Text(
                    text = formatDate(deadline),
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

@Composable
private fun getCheckboxColors(todoItem: TodoItem) = CheckboxDefaults.colors(
    checkedColor = MaterialTheme.colorScheme.secondary,
    checkmarkColor = Color.White,
    uncheckedColor = if (todoItem.importance == Importance.IMPORTANT) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }
).copy(
    uncheckedBoxColor = if (todoItem.done) {
        MaterialTheme.colorScheme.secondary
    } else if (todoItem.importance == Importance.IMPORTANT) {
        MaterialTheme.colorScheme.error.copy(alpha = 0.16f)
    } else {
        Color.Transparent
    }
)

@Composable
private fun getImportanceIcon(importance: Importance): Int? {
    return when (importance) {
        Importance.LOW -> R.drawable.low
        Importance.BASIC -> null
        Importance.IMPORTANT -> R.drawable.urgent
    }
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(date)
}

@Preview(showBackground = true)
@Composable
fun PreviewTodoItemView1() {
    val sampleTodoItem = TodoItem(
        "todo_1",
        "Buy grocery",
        Importance.BASIC,
        null,
        false,
        Date(),
        null
    )
    ToDoTheme(dynamicColor = false) {
        TodoItemView(todoItem = sampleTodoItem)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTodoItemView1Dark() {
    val sampleTodoItem = TodoItem(
        "todo_1",
        "Buy grocery",
        Importance.BASIC,
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
        Importance.BASIC,
        null,
        false,
        Date(),
        null
    )
    ToDoTheme(dynamicColor = false) {
        TodoItemView(todoItem = sampleTodoItem2)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTodoItemView2Dark() {
    val sampleTodoItem2 = TodoItem(
        "todo_2",
        "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрезается текст когда текст слишком длинный",
        Importance.BASIC,
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTodoItemView3Dark() {
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
        "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрезается текст когда текст слишком длинный",
        Importance.IMPORTANT,
        null,
        false,
        Date(),
        null
    )
    ToDoTheme(dynamicColor = false) {
        TodoItemView(todoItem = sampleTodoItem2)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTodoItemView4Dark() {
    val sampleTodoItem2 = TodoItem(
        "todo_4",
        "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрезается текст когда текст слишком длинный",
        Importance.IMPORTANT,
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
        Importance.BASIC,
        null,
        true,
        Date(),
        null
    )
    ToDoTheme(dynamicColor = false) {
        TodoItemView(todoItem = sampleTodoItem2)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTodoItemView5Dark() {
    val sampleTodoItem2 = TodoItem(
        "todo_5",
        "Купить что-то",
        Importance.BASIC,
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
        Importance.BASIC,
        Date(),
        false,
        Date(),
        null
    )
    ToDoTheme(dynamicColor = false) {
        TodoItemView(todoItem = sampleTodoItem2)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTodoItemView6Dark() {
    val sampleTodoItem2 = TodoItem(
        "todo_6",
        "Купить что-то",
        Importance.BASIC,
        Date(),
        false,
        Date(),
        null
    )
    ToDoTheme(dynamicColor = false) {
        TodoItemView(todoItem = sampleTodoItem2)
    }
}



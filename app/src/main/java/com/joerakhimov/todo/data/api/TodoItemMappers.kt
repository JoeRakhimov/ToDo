package com.joerakhimov.todo.data.api

import com.joerakhimov.todo.data.Importance
import com.joerakhimov.todo.data.TodoItem
import java.util.Date

fun TodoItemDto.toTodoItem(): TodoItem {
    return TodoItem(
        id = this.id,
        text = this.text,
        importance = Importance.valueOf(this.importance.uppercase()),
        deadline = this.deadline?.let { Date(it) },
        isCompleted = this.done,
        createdAt = Date(this.createdAt),
        modifiedAt = this.changedAt?.let { Date(it) }
    )
}

// Convert TodoItem to TodoItemDto
fun TodoItem.toTodoItemDto(): TodoItemDto {
    return TodoItemDto(
        id = this.id,
        text = this.text,
        importance = this.importance.name.lowercase(),
        deadline = this.deadline?.time,
        done = this.isCompleted,
        createdAt = this.createdAt.time,
        changedAt = this.modifiedAt?.time,
    )
}
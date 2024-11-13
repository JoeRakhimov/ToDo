package com.joerakhimov.todo.data.api

import com.joerakhimov.todo.data.model.Importance
import com.joerakhimov.todo.data.model.TodoItem
import com.joerakhimov.todo.data.dto.TodoItemDto
import java.util.Date

fun TodoItemDto.toTodoItem(): TodoItem {
    return TodoItem(
        id = this.id,
        text = this.text,
        importance = this.importance.let { Importance.valueOf(it.uppercase()) },
        deadline = this.deadline?.let { Date(it) },
        isCompleted = this.done,
        createdAt = Date(this.createdAt),
        modifiedAt = this.changedAt?.let { Date(it) },
        changedBy = this.lastUpdatedBy
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
        lastUpdatedBy = this.changedBy
    )
}
package com.joerakhimov.todo.data.source.api

import com.joerakhimov.todo.ui.model.Importance
import com.joerakhimov.todo.ui.model.TodoItem
import com.joerakhimov.todo.data.dto.TodoItemDto
import java.util.Date

fun TodoItemDto.toTodoItem(): TodoItem {
    return TodoItem(
        id = this.id,
        text = this.text,
        importance = this.importance.let { Importance.valueOf(it.uppercase()) },
        deadline = this.deadline?.let { Date(it) },
        done = this.done,
        createdAt = Date(this.createdAt),
        changedAt = this.changedAt?.let { Date(it) },
        lastUpdatedBy = this.lastUpdatedBy
    )
}

// Convert TodoItem to TodoItemDto
fun TodoItem.toTodoItemDto(): TodoItemDto {
    return TodoItemDto(
        id = this.id,
        text = this.text,
        importance = this.importance.name.lowercase(),
        deadline = this.deadline?.time,
        done = this.done,
        createdAt = this.createdAt.time,
        changedAt = this.changedAt?.time,
        lastUpdatedBy = this.lastUpdatedBy
    )
}
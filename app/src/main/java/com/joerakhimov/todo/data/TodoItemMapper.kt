package com.joerakhimov.todo.data

import java.util.Date

object TodoItemMapper {

    // Convert TodoItemDto to TodoItem
    fun toTodoItem(dto: TodoItemDto): TodoItem {
        return TodoItem(
            id = dto.id,
            text = dto.text,
            importance = Importance.valueOf(dto.importance.uppercase()),
            deadline = dto.deadline?.let { Date(it) },
            isCompleted = dto.done,
            createdAt = Date(dto.createdAt),
            modifiedAt = dto.changedAt?.let { Date(it) }
        )
    }

    // Convert TodoItem to TodoItemDto
    fun toTodoItemDto(todoItem: TodoItem): TodoItemDto {
        return TodoItemDto(
            id = todoItem.id,
            text = todoItem.text,
            importance = todoItem.importance.name.lowercase(),
            deadline = todoItem.deadline?.time,
            done = todoItem.isCompleted,
            createdAt = todoItem.createdAt.time,
            changedAt = todoItem.modifiedAt?.time,
        )
    }

}
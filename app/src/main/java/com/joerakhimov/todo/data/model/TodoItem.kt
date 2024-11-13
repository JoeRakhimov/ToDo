package com.joerakhimov.todo.data.model

import java.util.Date

data class TodoItem(
    val id: String, // идентификатор задания
    val text: String, // описания задания
    var importance: Importance, // важность дела
    var deadline: Date?, // дедлайн
    val isCompleted: Boolean, // флаг выполнения задания
    val createdAt: Date, // дата создания задания
    val modifiedAt: Date?, // дата изменения задания
    val changedBy: String = "" // пользователь, изменивший задание
)

enum class Importance {
    LOW,
    BASIC,
    IMPORTANT;
}

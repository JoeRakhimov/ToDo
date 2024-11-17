package com.joerakhimov.todo.ui.model

import java.util.Date

data class TodoItem(
    val id: String, // идентификатор задания
    val text: String, // описания задания
    val importance: Importance, // важность дела
    val deadline: Date?, // дедлайн
    val done: Boolean, // флаг выполнения задания
    val createdAt: Date, // дата создания задания
    val changedAt: Date?, // дата изменения задания
    val lastUpdatedBy: String = "" // пользователь, изменивший задание
)

enum class Importance {
    LOW,
    BASIC,
    IMPORTANT;
}

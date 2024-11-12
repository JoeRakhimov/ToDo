package com.joerakhimov.todo.data

import java.util.Date

data class TodoItem(
    val id: String, // идентификатор задания
    var text: String, // описания задания
    var importance: Importance, // важность дела
    var deadline: Date?, // дедлайн
    var isCompleted: Boolean, // флаг выполнения задания
    val createdAt: Date, // дата создания задания
    var modifiedAt: Date?, // дата изменения задания
    var changedBy: String = "" // пользователь, изменивший задание
)

enum class Importance {
    LOW,
    BASIC,
    IMPORTANT;
}

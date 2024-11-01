package com.joerakhimov.todo

import java.util.Date

data class TodoItem(
    val id: String, // идентификатор задания
    val text: String, // описания задания
    val importance: Importance, // важность дела
    val deadline: Date?, // дедлайн
    val isCompleted: Boolean, // флаг выполнения задания
    val createdAt: Date, // дата создания задания
    val modifiedAt: Date? // дата изменения задания
)

enum class Importance() {
    LOW,
    NORMAL,
    URGENT;
}

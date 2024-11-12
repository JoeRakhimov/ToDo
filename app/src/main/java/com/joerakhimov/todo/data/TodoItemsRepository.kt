package com.joerakhimov.todo.data

import android.content.SharedPreferences
import com.joerakhimov.todo.data.api.TaskRequestDto
import com.joerakhimov.todo.data.api.TodoApi
import com.joerakhimov.todo.data.api.toTodoItem
import com.joerakhimov.todo.data.api.toTodoItemDto
import java.util.Date

const val KEY_REVISION = "X-Last-Known-Revision"

class TodoItemsRepository(private val todoApi: TodoApi, private val preferences: SharedPreferences) {

    private val todoItems = mutableListOf(
        TodoItem("todo_1", "Buy groceries", Importance.BASIC, null, false, Date(), null),
        TodoItem("todo_2", "Finish Kotlin project", Importance.IMPORTANT, Date(System.currentTimeMillis() + 86400000), false, Date(), null),
        TodoItem("todo_3", "Call mom", Importance.BASIC, null, true, Date(), Date()),
        TodoItem("todo_4", "Schedule dentist appointment", Importance.IMPORTANT, Date(System.currentTimeMillis() + 604800000), false, Date(), null),
        TodoItem("todo_5", "Prepare the annual report for the department, including financial analysis, project updates, and team performance metrics", Importance.LOW, null, false, Date(), null),
        TodoItem("todo_6", "Exercise for 30 minutes", Importance.BASIC, null, true, Date(), Date()),
        TodoItem("todo_7", "Prepare presentation for work", Importance.IMPORTANT, Date(System.currentTimeMillis() + 432000000), false, Date(), null),
        TodoItem("todo_8", "Clean the house", Importance.BASIC, null, false, Date(), null),
        TodoItem("todo_9", "Water the plants", Importance.LOW, null, true, Date(), Date()),
        TodoItem("todo_10", "Finish the report", Importance.IMPORTANT, Date(System.currentTimeMillis() + 86400000), false, Date(), null),
        TodoItem("todo_11", "Buy new shoes", Importance.BASIC, null, false, Date(), null),
        TodoItem("todo_12", "Plan weekend trip", Importance.BASIC, Date(System.currentTimeMillis() + 259200000), false, Date(), null),
        TodoItem("todo_13", "Check emails", Importance.LOW, null, true, Date(), Date()),
        TodoItem("todo_14", "Organize the closet", Importance.BASIC, null, false, Date(), null),
        TodoItem("todo_15", "Attend yoga class", Importance.BASIC, Date(System.currentTimeMillis() + 86400000), false, Date(), null),
        TodoItem("todo_16", "Grocery shopping for next week", Importance.BASIC, Date(System.currentTimeMillis() + 604800000), false, Date(), null),
        TodoItem("todo_17", "Prepare dinner", Importance.BASIC, null, false, Date(), null),
        TodoItem("todo_18", "Finish online course", Importance.IMPORTANT, Date(System.currentTimeMillis() + 432000000), false, Date(), null),
        TodoItem("todo_19", "Clean the car", Importance.LOW, null, true, Date(), Date()),
        TodoItem("todo_20", "Buy birthday gift", Importance.IMPORTANT, Date(System.currentTimeMillis() + 604800000), false, Date(), null)
    )

    suspend fun getTodoItems(): List<TodoItem> {
        return todoApi.getTasks().also {
            preferences.edit().putInt(KEY_REVISION, it.revision ?: 0).apply()
        }.list.map { it.toTodoItem() }
    }

    suspend fun getTodoItem(id: String): TodoItem {
        return todoApi.getTask(id).also {
            preferences.edit().putInt(KEY_REVISION, it.revision ?: 0).apply()
        }.element.toTodoItem()
    }

    suspend fun addTodoItem(todoItem: TodoItem): TodoItem {
        val revision = preferences.getInt(KEY_REVISION, 0)
        val request = TaskRequestDto(todoItem.toTodoItemDto())
        return todoApi.addTask(revision, request).also {
            preferences.edit().putInt(KEY_REVISION, it.revision ?: 0).apply()
        }.element.toTodoItem()
    }

    suspend fun updateTodoItem(id: String, todoItem: TodoItem): TodoItem {
        val revision = preferences.getInt(KEY_REVISION, 0)
        val request = TaskRequestDto(todoItem.toTodoItemDto())
        return todoApi.updateTask(id, revision, request).also {
            preferences.edit().putInt(KEY_REVISION, it.revision ?: 0).apply()
        }.element.toTodoItem()
    }

    fun deleteTodoItem(todoItem: TodoItem) {
        todoItems.remove(todoItem)
    }

}
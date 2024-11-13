package com.joerakhimov.todo.data.repository

import android.content.SharedPreferences
import com.joerakhimov.todo.data.model.TodoItem
import com.joerakhimov.todo.data.dto.TodoRequestDto
import com.joerakhimov.todo.data.api.TodoApi
import com.joerakhimov.todo.data.api.toTodoItem
import com.joerakhimov.todo.data.api.toTodoItemDto
import com.joerakhimov.todo.data.db.TodoItemDao

const val KEY_REVISION = "X-Last-Known-Revision"
const val KEY_TODO_ITEMS_UP_TO_DATE = "todo_items_up_to_date"

class TodoItemsRepository(
    private val todoApi: TodoApi,
    private val todoItemDao: TodoItemDao,
    val connectivity: ConnectivityRepository,
    private val preferences: SharedPreferences
) {

    suspend fun getTodoItems(): List<TodoItem> {
        return if(connectivity.isNetworkAvailable()){
            todoApi.getTodoList().also {
                todoItemDao.insertAll(it.list)
                preferences.edit().putInt(KEY_REVISION, it.revision ?: 0).apply()
                preferences.edit().putBoolean(KEY_TODO_ITEMS_UP_TO_DATE, true).apply()
            }.list.map { it.toTodoItem() }
        } else {
            todoItemDao.getAllTodoItems().map { it.toTodoItem() }
        }
    }

    suspend fun getTodoItem(id: String): TodoItem {
        return todoApi.getTodo(id).also {
            preferences.edit().putInt(KEY_REVISION, it.revision).apply()
        }.element.toTodoItem()
    }

    suspend fun addTodoItem(todoItem: TodoItem): TodoItem {
        val revision = preferences.getInt(KEY_REVISION, 0)
        val request = TodoRequestDto(todoItem.toTodoItemDto())
        return todoApi.addTodo(revision, request).also {
            preferences.edit().putInt(KEY_REVISION, it.revision ?: 0).apply()
            preferences.edit().putBoolean(KEY_TODO_ITEMS_UP_TO_DATE, false).apply()
        }.element.toTodoItem()
    }

    suspend fun updateTodoItem(id: String, todoItem: TodoItem): TodoItem {
        val revision = preferences.getInt(KEY_REVISION, 0)
        val request = TodoRequestDto(todoItem.toTodoItemDto())
        return todoApi.updateTodo(id, revision, request).also {
            preferences.edit().putInt(KEY_REVISION, it.revision ?: 0).apply()
            preferences.edit().putBoolean(KEY_TODO_ITEMS_UP_TO_DATE, false).apply()
        }.element.toTodoItem()
    }

    suspend fun deleteTodoItem(id: String): TodoItem {
        val revision = preferences.getInt(KEY_REVISION, 0)
        return todoApi.deleteTodo(id, revision).also {
            preferences.edit().putInt(KEY_REVISION, it.revision).apply()
            preferences.edit().putBoolean(KEY_TODO_ITEMS_UP_TO_DATE, false).apply()
        }.element.toTodoItem()
    }

    fun isTodoItemsUpToDate(): Boolean {
        return preferences.getBoolean(KEY_TODO_ITEMS_UP_TO_DATE, false)
    }

}
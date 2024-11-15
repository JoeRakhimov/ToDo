package com.joerakhimov.todo.data.repository

import android.content.SharedPreferences
import com.joerakhimov.todo.ui.model.TodoItem
import com.joerakhimov.todo.data.dto.TodoRequestDto
import com.joerakhimov.todo.data.source.api.TodoApi
import com.joerakhimov.todo.data.source.api.toTodoItem
import com.joerakhimov.todo.data.source.api.toTodoItemDto
import com.joerakhimov.todo.data.source.db.TodoItemDao
import com.joerakhimov.todo.ui.workmanager.UpdateTodoItemsWorker
import java.io.IOException

const val KEY_REVISION = "X-Last-Known-Revision"
const val KEY_TODO_ITEMS_UP_TO_DATE = "todo_items_up_to_date"

class TodoItemsRepository(
    private val todoApi: TodoApi,
    private val todoItemDao: TodoItemDao,
    private val connectivity: ConnectivityRepository,
    private val preferences: SharedPreferences
) {

    suspend fun getTodoItems(): Pair<List<TodoItem>, Boolean> {
        return if(connectivity.isNetworkAvailable()){
            val result = todoApi.getTodoList().also {
                todoItemDao.deleteAll()
                todoItemDao.insertAll(it.list)
                preferences.edit().putInt(KEY_REVISION, it.revision ?: 0).apply()
                preferences.edit().putBoolean(KEY_TODO_ITEMS_UP_TO_DATE, true).apply()
                UpdateTodoItemsWorker.schedule()
            }.list.map { it.toTodoItem() }
            Pair(result, true)
        } else {
            if(isTodoItemsUpToDate()){ // check if data has been loaded before from the API
                val result = todoItemDao.getAllTodoItems().map { it.toTodoItem() }
                Pair(result, false)
            } else {
                throw IOException("No internet connection")
            }
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
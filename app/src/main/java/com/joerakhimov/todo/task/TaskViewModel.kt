package com.joerakhimov.todo.task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.joerakhimov.todo.data.TodoItem
import com.joerakhimov.todo.data.TodoItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(
    private val todoItemsRepository: TodoItemsRepository,
    private val todoItemId: String
): ViewModel() {

    private val _todoItem = MutableStateFlow<TodoItem?>(null)
    val todoItem: StateFlow<TodoItem?> = _todoItem

    init {
        fetchTodoItem()
    }

    private fun fetchTodoItem() {
        viewModelScope.launch {
            try {
                val todoItem = todoItemsRepository.getTodoItem(todoItemId)
                _todoItem.value = todoItem
            } catch (e: Exception) {
                _todoItem.value = null
            }
        }
    }

}

class TaskViewModelFactory(
    private val todoItemsRepository: TodoItemsRepository,
    private val todoItemId: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(todoItemsRepository, todoItemId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
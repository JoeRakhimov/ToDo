package com.joerakhimov.todo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TodoViewModel : ViewModel() {
    private val repository = TodoItemsRepository()

    // Use MutableStateFlow to hold the list of tasks
    private val _todoItems = MutableStateFlow(repository.getTodoItems())
    val todoItems: StateFlow<List<TodoItem>> = _todoItems

    // Function to add a new task
    fun addTodoItem(todoItem: TodoItem) {
        repository.addTodoItem(todoItem)
        _todoItems.value = repository.getTodoItems()
    }

    // Function to delete a task
    fun deleteTodoItem(todoItem: TodoItem) {
        repository.deleteTodoItem(todoItem)
        _todoItems.value = repository.getTodoItems()
    }
}
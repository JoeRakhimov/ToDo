package com.joerakhimov.todo.tasks

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.joerakhimov.todo.data.TodoItem
import com.joerakhimov.todo.data.TodoItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TasksViewModel(private val todoItemsRepository: TodoItemsRepository): ViewModel() {

    private val _todoItems = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoItems: StateFlow<List<TodoItem>> = _todoItems

    val snackbarHostState = SnackbarHostState()

    init {
        fetchTodoItems()
    }

    private fun fetchTodoItems() {
        viewModelScope.launch {
            try {
                val items = todoItemsRepository.getTodoItems() // Suspend function
                _todoItems.value = items
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Something went wrong")
            }
        }
    }

    fun updateTodoItems() {
        viewModelScope.launch {
            if (!todoItemsRepository.isTodoItemsUpToDate()) {
                fetchTodoItems()
            }
        }
    }

}

class TasksViewModelFactory(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            return TasksViewModel(todoItemsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
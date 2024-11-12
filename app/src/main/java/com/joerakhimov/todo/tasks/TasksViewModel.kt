package com.joerakhimov.todo.tasks

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.joerakhimov.todo.data.TodoItem
import com.joerakhimov.todo.data.TodoItemsRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeoutException
import retrofit2.HttpException
import java.io.IOException
import java.util.Date

class TasksViewModel(private val todoItemsRepository: TodoItemsRepository) : ViewModel() {

    private val _todoItems = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoItems: StateFlow<List<TodoItem>> = _todoItems

    val snackbarHostState = SnackbarHostState()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch(Dispatchers.IO) {
            val errorMessage: String = when (exception) {
                is IOException -> {
                    "Произошла ошибка сети."
                }

                is HttpException -> {
                    "Произошла ошибка HTTP с кодом статуса: ${exception.code()}"
                }

                is TimeoutException -> {
                    "Запрос превысил время ожидания. Пожалуйста, попробуйте снова."
                }

                else -> {
                    "Что-то пошло не так" // Something went wrong
                }
            }

            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    init {
        fetchTodoItems()
    }

    private fun fetchTodoItems() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val items = todoItemsRepository.getTodoItems() // Suspend function
            _todoItems.value = items
        }
    }

    fun updateTodoItems() {
        if (!todoItemsRepository.isTodoItemsUpToDate()) {
            fetchTodoItems()
        }
    }

    fun completeTodoItem(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val updatedList = _todoItems.value.map {
                if (it.id == todoItem.id) todoItem
                else it
            }
            _todoItems.value = updatedList
            todoItemsRepository.updateTodoItem(
                todoItem.id, todoItem.copy(
                    modifiedAt = Date()
                )
            )
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
package com.joerakhimov.todo.ui.tasks

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.joerakhimov.todo.data.TodoItem
import com.joerakhimov.todo.data.TodoItemsRepository
import com.joerakhimov.todo.ui.ScreenState
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

    private val _state = MutableStateFlow<ScreenState<List<TodoItem>>>(ScreenState.Loading)
    val state: StateFlow<ScreenState<List<TodoItem>>> = _state

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
            _state.value = ScreenState.Error(errorMessage)
        }
    }

    init {
        fetchTodoItems()
    }

    fun fetchTodoItems() {
        _state.value = ScreenState.Loading
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val items = todoItemsRepository.getTodoItems() // Suspend function
            _state.value = ScreenState.Success(items)
        }
    }

    fun updateTodoItems() {
        if (!todoItemsRepository.isTodoItemsUpToDate()) {
            fetchTodoItems()
        }
    }

    fun completeTodoItem(todoItem: TodoItem) {
        val currentState = state.value
        if (currentState is ScreenState.Success) {
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                val updatedList = currentState.data.map {
                    if (it.id == todoItem.id) todoItem
                    else it
                }
                _state.value = ScreenState.Success(updatedList)
                todoItemsRepository.updateTodoItem(
                    todoItem.id, todoItem.copy(
                        modifiedAt = Date()
                    )
                )
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
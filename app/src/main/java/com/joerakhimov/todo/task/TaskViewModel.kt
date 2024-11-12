package com.joerakhimov.todo.task

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.joerakhimov.todo.data.Importance
import com.joerakhimov.todo.data.TodoItem
import com.joerakhimov.todo.data.TodoItemsRepository
import com.joerakhimov.todo.navigation.DEFAULT_TASK_ID
import com.joerakhimov.todo.ui.ScreenState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeoutException
import retrofit2.HttpException

class TaskViewModel(
    private val todoItemsRepository: TodoItemsRepository,
    private val todoItemId: String
) : ViewModel() {

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

    val snackbarHostState = SnackbarHostState()

    private val _todoItem = MutableStateFlow<ScreenState<TodoItem>>(
        ScreenState.Success(
            TodoItem(
                id = generateUUID(),
                text = "",
                importance = Importance.BASIC,
                deadline = null,
                isCompleted = false,
                createdAt = Date(),
                modifiedAt = null,
                changedBy = "device_123"
            )
        )
    )
    val todoItem: StateFlow<ScreenState<TodoItem>> = _todoItem

    private val _todoItemSaved = MutableStateFlow(false)
    val todoItemSaved: StateFlow<Boolean> = _todoItemSaved

    init {
        if (todoItemId != DEFAULT_TASK_ID) {
            fetchTodoItem()
        }
    }

    private fun fetchTodoItem() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val todoItem = todoItemsRepository.getTodoItem(todoItemId)
            _todoItem.value = ScreenState.Success(todoItem)
        }
    }

    fun addTodoItem(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (todoItem.text.isEmpty()) {
                snackbarHostState.showSnackbar("Описание не может быть пустым")
                return@launch
            }
            todoItemsRepository.addTodoItem(
                todoItem.copy(
                    modifiedAt = Date(),
                )
            )
            _todoItemSaved.value = true
        }
    }

    fun updateTodoItemDescription(description: String) {
        val currentState = todoItem.value
        if (currentState is ScreenState.Success) {
            val updatedTodo = currentState.data.copy(text = description)
            _todoItem.value = ScreenState.Success(updatedTodo)
        }
    }

    fun updateTodoItem(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (todoItem.text.isEmpty()) {
                snackbarHostState.showSnackbar("Описание не может быть пустым")
                return@launch
            }
            todoItemsRepository.updateTodoItem(
                todoItemId, todoItem.copy(
                    modifiedAt = Date(),
                )
            )
            _todoItemSaved.value = true
        }
    }

    fun deleteTodoItem(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            todoItemsRepository.deleteTodoItem(todoItemId)
            _todoItemSaved.value = true
        }
    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
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
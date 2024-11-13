package com.joerakhimov.todo.ui.task

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.joerakhimov.todo.data.Importance
import com.joerakhimov.todo.data.TodoItem
import com.joerakhimov.todo.data.TodoItemsRepository
import com.joerakhimov.todo.navigation.DEFAULT_TODO_ID
import com.joerakhimov.todo.ui.common.State
import com.joerakhimov.todo.ui.common.getHumanReadableErrorMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class TaskViewModel(
    private val todoItemsRepository: TodoItemsRepository,
    private val todoItemId: String
) : ViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch(Dispatchers.IO) {
            snackbarHostState.showSnackbar(exception.getHumanReadableErrorMessage())
        }
    }

    val snackbarHostState = SnackbarHostState()

    private val _state = MutableStateFlow<State<TodoItem>>(
        State.Success(
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
    val state: StateFlow<State<TodoItem>> = _state

    private val _operationOnTodoCompleted = MutableStateFlow(false)
    val operationOnTodoCompleted: StateFlow<Boolean> = _operationOnTodoCompleted

    init {
        if (todoItemId != DEFAULT_TODO_ID) {
            fetchTodoItem()
        }
    }

    fun fetchTodoItem() {
        _state.value = State.Loading
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val todoItem = todoItemsRepository.getTodoItem(todoItemId)
                _state.value = State.Success(todoItem)
            } catch (e: Exception) {
                _state.value = State.Error(e.getHumanReadableErrorMessage())
            }
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
            _operationOnTodoCompleted.value = true
        }
    }

    fun updateTodoItemDescription(description: String) {
        val currentState = state.value
        if (currentState is State.Success) {
            val updatedTodo = currentState.data.copy(text = description)
            _state.value = State.Success(updatedTodo)
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
            _operationOnTodoCompleted.value = true
        }
    }

    fun deleteTodoItem(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            todoItemsRepository.deleteTodoItem(todoItemId)
            _operationOnTodoCompleted.value = true
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
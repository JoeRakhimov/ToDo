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
                changedBy = "device123"
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val todoItem = todoItemsRepository.getTodoItem(todoItemId)
                _todoItem.value = ScreenState.Success(todoItem)
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Something went wrong")
            }
        }
    }

    fun addTodoItem(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (todoItem.text.isEmpty()) {
                    snackbarHostState.showSnackbar("Description cannot be empty")
                    return@launch
                }
                todoItemsRepository.addTodoItem(
                    todoItem.copy(
                        modifiedAt = Date(),
                    )
                )
                _todoItemSaved.value = true
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Something went wrong")
            }
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (todoItem.text.isEmpty()) {
                    snackbarHostState.showSnackbar("Description cannot be empty")
                    return@launch
                }
                todoItemsRepository.updateTodoItem(
                    todoItemId, todoItem.copy(
                        modifiedAt = Date(),
                    )
                )
                _todoItemSaved.value = true
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Something went wrong")
            }
        }
    }

    fun deleteTodoItem(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                todoItemsRepository.deleteTodoItem(todoItemId)
                _todoItemSaved.value = true
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Something went wrong")
            }
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
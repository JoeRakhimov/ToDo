package com.joerakhimov.todo.ui.task

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.joerakhimov.todo.data.repository.ConnectivityRepository
import com.joerakhimov.todo.data.model.Importance
import com.joerakhimov.todo.data.model.TodoItem
import com.joerakhimov.todo.data.repository.TodoItemsRepository
import com.joerakhimov.todo.navigation.DEFAULT_TODO_ID
import com.joerakhimov.todo.ui.common.State
import com.joerakhimov.todo.ui.common.getHumanReadableErrorMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch(Dispatchers.IO) {
            snackbarHostState.showSnackbar(exception.getHumanReadableErrorMessage())
        }
    }

    private val _state = MutableStateFlow<State<TodoItem>>(
        State.Success(
            TodoItem(
                id = generateUUID(),
                text = "",
                importance = Importance.BASIC,
                deadline = null,
                done = false,
                createdAt = Date(),
                changedAt = null,
                lastUpdatedBy = "device_123"
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

    private var fetchTodoItemJob: Job? = null
    fun fetchTodoItem() {
        _state.value = State.Loading
        fetchTodoItemJob?.cancel() // to cancel previous job to avoid automatic retry after successful manual retry
        todoItemsRepository.connectivity.unregister()
        fetchTodoItemJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val todoItem = todoItemsRepository.getTodoItem(todoItemId)
                _state.value = State.Success(todoItem)
            } catch (e: Exception) {
                observeConnectivity()
                var secondsBeforeRetry = 30
                repeat(secondsBeforeRetry) {
                    _state.value = State.Error("${e.getHumanReadableErrorMessage()}. Повторная попытка через $secondsBeforeRetry секунд ...")
                    delay(1000)
                    secondsBeforeRetry -= 1
                }
                fetchTodoItem()
            }
        }
    }

    private suspend fun observeConnectivity() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            todoItemsRepository.connectivity.register()
            todoItemsRepository.connectivity.isConnected.collect { isConnected ->
                if(isConnected){
                    if(state.value is State.Error){
                        fetchTodoItem()
                    }
                }
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
                    changedAt = Date(),
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
                    changedAt = Date(),
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

    override fun onCleared() {
        super.onCleared()
        todoItemsRepository.connectivity.unregister()
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
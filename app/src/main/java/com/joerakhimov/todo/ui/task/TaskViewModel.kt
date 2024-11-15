package com.joerakhimov.todo.ui.task

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.joerakhimov.todo.data.repository.ConnectivityRepository
import com.joerakhimov.todo.ui.model.Importance
import com.joerakhimov.todo.ui.model.TodoItem
import com.joerakhimov.todo.data.repository.TodoItemsRepository
import com.joerakhimov.todo.data.source.util.ExceptionMessageUtil
import com.joerakhimov.todo.ui.common.SnackbarMessage
import com.joerakhimov.todo.ui.navigation.DEFAULT_TODO_ID
import com.joerakhimov.todo.ui.common.State
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Date
import java.util.UUID

class TaskViewModel(
    private val todoItemsRepository: TodoItemsRepository,
    private val connectivityRepository: ConnectivityRepository,
    private val exceptionMessageUtil: ExceptionMessageUtil,
    private val todoItemId: String
) : ViewModel() {

    private val _snackbarMessage = MutableStateFlow<SnackbarMessage?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch(Dispatchers.IO) {
            _snackbarMessage.value = SnackbarMessage.TextMessage(exceptionMessageUtil.getHumanReadableErrorMessage(exception))
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
        fetchTodoItemJob?.takeIf { it.isActive }?.cancel() // to cancel previous job to avoid automatic retry after successful manual retry
        fetchTodoItemJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val todoItem = todoItemsRepository.getTodoItem(todoItemId)
                _state.value = State.Success(todoItem)
            } catch (e: Exception) {
                observeConnectivity()
                var secondsBeforeRetry = 30
                repeat(secondsBeforeRetry) {
                    _state.value =
                        State.Error("${exceptionMessageUtil.getHumanReadableErrorMessage(e)}.", e, secondsBeforeRetry)
                    delay(1000)
                    secondsBeforeRetry--
                }
                fetchTodoItem()
            }
        }
    }

    private var observeConnectivityJob: Job? = null
    private suspend fun observeConnectivity() {
        observeConnectivityJob?.takeIf { it.isActive }?.cancel()
        observeConnectivityJob = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            connectivityRepository.register()
            connectivityRepository.isConnected.collect { isConnected ->
                if (isConnected) {
                    val state = state.value
                    if(state is State.Error){
                        if(state.exception is IOException){
                            fetchTodoItem()
                        }
                    }
                }
            }
        }
    }

    fun addTodoItem(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (todoItem.text.isEmpty()) {
                _snackbarMessage.value = SnackbarMessage.TaskDescriptionCannotBeEmpty
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

    fun updateTodoItemDeadline(deadlineDate: Date?) {
        val currentState = state.value
        if (currentState is State.Success) {
            val updatedTodo = currentState.data.copy(deadline = deadlineDate)
            _state.value = State.Success(updatedTodo)
        }
    }

    fun updateTodoImportance(importance: Importance) {
        val currentState = state.value
        if (currentState is State.Success) {
            val updatedTodo = currentState.data.copy(importance = importance)
            _state.value = State.Success(updatedTodo)
        }
    }

    fun updateTodoItem(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (todoItem.text.isEmpty()) {
                _snackbarMessage.value = SnackbarMessage.TaskDescriptionCannotBeEmpty
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

    fun clearSnackbarMessage(){
        _snackbarMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        observeConnectivityJob?.takeIf { it.isActive }?.cancel()
        connectivityRepository.unregister()
    }

}

class TaskViewModelFactory(
    private val todoItemsRepository: TodoItemsRepository,
    private val connectivityRepository: ConnectivityRepository,
    private val exceptionMessageUtil: ExceptionMessageUtil,
    private val todoItemId: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(todoItemsRepository, connectivityRepository, exceptionMessageUtil, todoItemId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
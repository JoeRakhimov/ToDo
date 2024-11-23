package com.joerakhimov.todo.ui.screens.task

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
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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
import javax.inject.Inject

class TaskViewModel @AssistedInject constructor(
    private val todoItemsRepository: TodoItemsRepository,
    private val connectivityRepository: ConnectivityRepository,
    private val exceptionMessageUtil: ExceptionMessageUtil,
    @Assisted private val todoItemId: String
) : ViewModel() {

    private val _todoItemState = MutableStateFlow<State<TodoItem>>(State.Loading)
    val todoItemState: StateFlow<State<TodoItem>> = _todoItemState

    private val _operationOnTodoCompleted = MutableStateFlow(false)
    val operationOnTodoCompleted: StateFlow<Boolean> = _operationOnTodoCompleted

    private val _snackbarMessage = MutableStateFlow<SnackbarMessage?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch(Dispatchers.IO) {
            _snackbarMessage.value = SnackbarMessage.TextMessage(
                exceptionMessageUtil.getHumanReadableErrorMessage(exception)
            )
        }
    }

    init {
        if (todoItemId == DEFAULT_TODO_ID) {
            _todoItemState.value = State.Success(
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
        } else {
            fetchTodoItem()
        }
    }

    private var fetchTodoItemJob: Job? = null
    fun fetchTodoItem() {
        _todoItemState.value = State.Loading
        fetchTodoItemJob?.takeIf { it.isActive }
            ?.cancel() // to cancel previous job to avoid automatic retry after successful manual retry
        fetchTodoItemJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val todoItem = todoItemsRepository.getTodoItem(todoItemId)
                _todoItemState.value = State.Success(todoItem)
            } catch (e: Exception) {
                observeConnectivity()
                var secondsBeforeRetry = 30
                repeat(secondsBeforeRetry) {
                    _todoItemState.value =
                        State.Error(
                            "${exceptionMessageUtil.getHumanReadableErrorMessage(e)}.",
                            e,
                            secondsBeforeRetry
                        )
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
            connectivityRepository.isConnectedFlow.collect { isConnected ->
                if (isConnected) {
                    val state = todoItemState.value
                    if (state is State.Error) {
                        if (state.exception is IOException) {
                            fetchTodoItem()
                        }
                    }
                }
            }
        }
    }

    fun addTodoItem() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val currentState = todoItemState.value
            if (currentState is State.Success) {
                if (currentState.data.text.isEmpty()) {
                    _snackbarMessage.value = SnackbarMessage.TaskDescriptionCannotBeEmpty
                    return@launch
                }
                todoItemsRepository.addTodoItem(
                    currentState.data.copy(
                        changedAt = Date(),
                    )
                )
                _operationOnTodoCompleted.value = true
            }

        }
    }

    fun updateTodoItemDescription(description: String) {
        val currentState = todoItemState.value
        if (currentState is State.Success) {
            val updatedTodo = currentState.data.copy(text = description)
            _todoItemState.value = State.Success(updatedTodo)
        }
    }

    fun updateTodoItemDeadline(deadlineDate: Date?) {
        val currentState = todoItemState.value
        if (currentState is State.Success) {
            val updatedTodo = currentState.data.copy(deadline = deadlineDate)
            _todoItemState.value = State.Success(updatedTodo)
        }
    }

    fun updateTodoImportance(importance: Importance) {
        val currentState = todoItemState.value
        if (currentState is State.Success) {
            val updatedTodo = currentState.data.copy(importance = importance)
            _todoItemState.value = State.Success(updatedTodo)
        }
    }

    fun updateTodoItem() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val currentState = todoItemState.value
            if (currentState is State.Success) {
                if (currentState.data.text.isEmpty()) {
                    _snackbarMessage.value = SnackbarMessage.TaskDescriptionCannotBeEmpty
                    return@launch
                }
                todoItemsRepository.updateTodoItem(
                    todoItemId, currentState.data.copy(
                        changedAt = Date(),
                    )
                )
                _operationOnTodoCompleted.value = true
            }
        }
    }

    fun deleteTodoItem() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            todoItemsRepository.deleteTodoItem(todoItemId)
            _operationOnTodoCompleted.value = true
        }
    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        observeConnectivityJob?.takeIf { it.isActive }?.cancel()
        connectivityRepository.unregister()
    }

}

@AssistedFactory
interface AssistedTaskViewModelFactory {
    fun create(todoId: String): TaskViewModel
}

class TaskViewModelFactory @Inject constructor(
    private val assistedFactory: AssistedTaskViewModelFactory,
    private val todoId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return assistedFactory.create(todoId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
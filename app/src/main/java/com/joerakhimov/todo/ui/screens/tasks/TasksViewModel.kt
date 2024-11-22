package com.joerakhimov.todo.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.joerakhimov.todo.data.repository.ConnectivityRepository
import com.joerakhimov.todo.ui.model.TodoItem
import com.joerakhimov.todo.data.repository.TodoItemsRepository
import com.joerakhimov.todo.data.source.util.ExceptionMessageUtil
import com.joerakhimov.todo.ui.common.SnackbarMessage
import com.joerakhimov.todo.ui.common.State
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

class TasksViewModel @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository,
    private val connectivityRepository: ConnectivityRepository,
    private val exceptionMessageUtil: ExceptionMessageUtil
) : ViewModel() {

    private val _state = MutableStateFlow<State<List<TodoItem>>>(State.Loading)
    val state: StateFlow<State<List<TodoItem>>> = _state

    private val _snackbarMessage = MutableStateFlow<SnackbarMessage?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch(Dispatchers.IO) {
            var secondsBeforeRetry = 30
            repeat(secondsBeforeRetry) { // count 30 seconds
                _state.value = State.Error("${exceptionMessageUtil.getHumanReadableErrorMessage(exception)}.", exception, secondsBeforeRetry)
                delay(1000)
                secondsBeforeRetry -= 1
            }
            fetchTodoItems()
        }
    }

    init {
        fetchTodoItems()
    }

    private var fetchTodoItemsJob: Job? = null
    fun fetchTodoItems() {
        _state.value = State.Loading
        fetchTodoItemsJob?.cancel() // to cancel previous job to avoid automatic retry after successful manual retry
        connectivityRepository.unregister()
        fetchTodoItemsJob = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val items = todoItemsRepository.getTodoItems()
                _state.value = State.Success(items.first)
                if (!items.second) {
                    _snackbarMessage.value = SnackbarMessage.ShowingCachedData
                }
            } catch (e: Exception) {
                observeConnectivity()
                var secondsBeforeRetry = 30
                repeat(secondsBeforeRetry) {
                    _state.value = State.Error("${exceptionMessageUtil.getHumanReadableErrorMessage(e)}.", e, secondsBeforeRetry)
                    delay(1000)
                    secondsBeforeRetry -= 1
                }
                fetchTodoItems()
            }
        }
    }

    private suspend fun observeConnectivity() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler){
            connectivityRepository.register()
            connectivityRepository.isConnectedFlow.collect { isConnected ->
                if(isConnected){
                    if(state.value is State.Error){
                        fetchTodoItems()
                    }
                }
            }
        }
    }

    fun updateTodoItems() {
        if (!todoItemsRepository.isTodoItemsUpToDate()) {
            fetchTodoItems()
        }
    }

    fun completeTodoItem(todoItem: TodoItem) {
        val currentState = state.value
        if (currentState is State.Success) {
            viewModelScope.launch(Dispatchers.IO) {
                val listBeforeUpdate = currentState.data
                val updatedList = currentState.data.map {
                    if (it.id == todoItem.id){
                        todoItem
                    }
                    else it
                }
                _state.value = State.Success(updatedList)
                try {
                    todoItemsRepository.updateTodoItem(
                        todoItem.id, todoItem.copy(
                            changedAt = Date()
                        )
                    )
                } catch (e: Exception) {
                    _state.value = State.Success(listBeforeUpdate)
                    _snackbarMessage.value = SnackbarMessage.TextMessage(
                        exceptionMessageUtil.getHumanReadableErrorMessage(e)
                    )
                }
            }
        }
    }

    fun clearSnackbarMessage(){
        _snackbarMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        connectivityRepository.unregister()
    }

}

class TasksViewModelFactory @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository,
    private val connectivityRepository: ConnectivityRepository,
    private val exceptionMessageUtil: ExceptionMessageUtil
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            return TasksViewModel(todoItemsRepository, connectivityRepository, exceptionMessageUtil) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
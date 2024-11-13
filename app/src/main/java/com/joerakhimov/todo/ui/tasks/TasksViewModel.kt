package com.joerakhimov.todo.ui.tasks

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.joerakhimov.todo.data.repository.ConnectivityRepository
import com.joerakhimov.todo.data.model.TodoItem
import com.joerakhimov.todo.data.repository.TodoItemsRepository
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

class TasksViewModel(
    private val todoItemsRepository: TodoItemsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<State<List<TodoItem>>>(State.Loading)
    val state: StateFlow<State<List<TodoItem>>> = _state

    val snackbarHostState = SnackbarHostState()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch(Dispatchers.IO) {
            var secondsBeforeRetry = 30
            repeat(secondsBeforeRetry * 1000) { // count 30 seconds
                _state.value = State.Error("${exception.getHumanReadableErrorMessage()}. Повторная попытка через $secondsBeforeRetry секунд")
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
        todoItemsRepository.connectivity.unregister()
        fetchTodoItemsJob = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val items = todoItemsRepository.getTodoItems()
                _state.value = State.Success(items.first)
                if (!items.second) {
                    snackbarHostState.showSnackbar("Отображаются ранее сохраненные данные.")
                }
            } catch (e: Exception) {
                observeConnectivity()
                var secondsBeforeRetry = 30
                repeat(secondsBeforeRetry) {
                    _state.value = State.Error("${e.getHumanReadableErrorMessage()}. Повторная попытка через $secondsBeforeRetry секунд ...")
                    delay(1000)
                    secondsBeforeRetry -= 1
                }
                fetchTodoItems()
            }
        }
    }

    private suspend fun observeConnectivity() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler){
            todoItemsRepository.connectivity.register()
            todoItemsRepository.connectivity.isConnected.collect { isConnected ->
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
                val updatedList = currentState.data.map {
                    if (it.id == todoItem.id) todoItem
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
                    snackbarHostState.showSnackbar(e.getHumanReadableErrorMessage())
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        todoItemsRepository.connectivity.unregister()
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
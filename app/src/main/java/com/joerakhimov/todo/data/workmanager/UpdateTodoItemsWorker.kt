package com.joerakhimov.todo.data.workmanager

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.joerakhimov.todo.data.api.ApiServiceProvider
import com.joerakhimov.todo.data.db.TodoDatabase
import com.joerakhimov.todo.data.repository.ConnectivityRepository
import com.joerakhimov.todo.data.repository.TodoItemsRepository
import com.joerakhimov.todo.navigation.PREFERENCES_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

const val UPDATE_TODO_ITEMS_WORK_NAME = "UPDATE_TODO_ITEMS_WORK_NAME"

class UpdateTodoItemsWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        fun schedule() {
            val workRequest =
                PeriodicWorkRequest.Builder(UpdateTodoItemsWorker::class.java, 8, TimeUnit.HOURS)
                    .setInitialDelay(8, TimeUnit.HOURS)
                    .setConstraints(
                        Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                    .build()
            WorkManager.getInstance()
                .enqueueUniquePeriodicWork(
                    UPDATE_TODO_ITEMS_WORK_NAME,
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    workRequest
                )
        }
    }

    override suspend fun doWork(): Result {
        return try {
            // Your data updating logic here
            updateData()
            // Return success after the task completes
            Result.success()
        } catch (e: Exception) {
            // Handle failure
            Result.failure()
        }
    }

    private suspend fun updateData() {
        val todoApi = ApiServiceProvider.provideTodoApi(appContext)
        val dao = TodoDatabase.getDatabase(appContext).todoItemDao()
        val connectivityRepository = ConnectivityRepository(appContext)
        val preferences = appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val todoItemsRepository =
            TodoItemsRepository(todoApi, dao, connectivityRepository, preferences)
        withContext(Dispatchers.IO) {
            todoItemsRepository.getTodoItems()
        }
    }

}
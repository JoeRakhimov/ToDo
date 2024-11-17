package com.joerakhimov.todo.ui.workmanager

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.joerakhimov.todo.data.repository.TodoItemsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

const val UPDATE_TODO_ITEMS_WORK_NAME = "UPDATE_TODO_ITEMS_WORK_NAME"

class UpdateTodoItemsWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val todoItemsRepository: TodoItemsRepository
) :
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
            // Use the injected repository to update data
            withContext(Dispatchers.IO) {
                todoItemsRepository.getTodoItems()
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}
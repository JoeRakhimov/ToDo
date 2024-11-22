package com.joerakhimov.todo.ui.di

import androidx.lifecycle.ViewModelProvider
import com.joerakhimov.todo.ui.screens.task.TaskViewModelFactory
import com.joerakhimov.todo.ui.screens.tasks.TasksViewModelFactory
import dagger.Binds
import dagger.Module


@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindsTasksViewModelFactory(factory: TasksViewModelFactory): ViewModelProvider.Factory

    @Binds
    abstract fun bindsTaskViewModelFactory(factory: TaskViewModelFactory): ViewModelProvider.Factory

}

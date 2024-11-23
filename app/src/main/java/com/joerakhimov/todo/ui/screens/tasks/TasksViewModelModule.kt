package com.joerakhimov.todo.ui.screens.tasks

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module


@Module
abstract class TasksViewModelModule {

    @Binds
    abstract fun bindsTasksViewModelFactory(factory: TasksViewModelFactory): ViewModelProvider.Factory

}

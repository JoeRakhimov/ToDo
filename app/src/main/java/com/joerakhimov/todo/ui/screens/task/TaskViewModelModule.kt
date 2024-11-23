package com.joerakhimov.todo.ui.screens.task

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module


@Module
abstract class TaskViewModelModule {

    @TaskScope
    @Binds
    abstract fun bindsTaskViewModelFactory(factory: TaskViewModelFactory): ViewModelProvider.Factory

}

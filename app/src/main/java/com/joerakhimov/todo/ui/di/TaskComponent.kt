package com.joerakhimov.todo.ui.di

import com.joerakhimov.todo.ui.screens.task.TaskViewModelFactory
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent
interface TaskComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance todoId: String): TaskComponent
    }

    fun provideTaskViewModelFactory(): TaskViewModelFactory

}
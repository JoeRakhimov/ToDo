package com.joerakhimov.todo.ui.screens.task

import dagger.BindsInstance
import dagger.Subcomponent

@TaskScope
@Subcomponent(modules = [
    TaskViewModelModule::class,
])
interface TaskComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance todoId: String): TaskComponent
    }

    fun provideTaskViewModelFactory(): TaskViewModelFactory

}
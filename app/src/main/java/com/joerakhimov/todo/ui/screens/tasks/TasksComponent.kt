package com.joerakhimov.todo.ui.screens.tasks

import dagger.Subcomponent

@Subcomponent(modules = [
    TasksViewModelModule::class,
])
interface TasksComponent {

    fun provideTasksViewModelFactory(): TasksViewModelFactory

}
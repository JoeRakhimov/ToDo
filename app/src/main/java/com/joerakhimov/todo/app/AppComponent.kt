package com.joerakhimov.todo.app

import android.app.Application
import com.joerakhimov.todo.data.source.api.ApiModule
import com.joerakhimov.todo.data.source.db.DatabaseModule
import com.joerakhimov.todo.data.source.prefs.PrefsModule
import com.joerakhimov.todo.ui.MainActivity
import com.joerakhimov.todo.ui.screens.task.TaskComponent
import com.joerakhimov.todo.ui.screens.tasks.TasksComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    AppModule::class,
    ApiModule::class,
    DatabaseModule::class,
    PrefsModule::class
])
@Singleton
interface AppComponent {

    // Expose the injection points
    fun inject(activity: MainActivity)
    fun providesTaskComponent(): TasksComponent
    fun provideTaskComponentFactory(): TaskComponent.Factory

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }

}
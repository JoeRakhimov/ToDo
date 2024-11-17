package com.joerakhimov.todo.data.source.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TodoDatabase::class.java,
            TODO_DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideTodoItemDao(database: TodoDatabase): TodoItemDao {
        return database.todoItemDao()
    }

}
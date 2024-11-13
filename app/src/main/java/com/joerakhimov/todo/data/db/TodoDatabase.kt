package com.joerakhimov.todo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.joerakhimov.todo.data.dto.TodoItemDto

const val TODO_DATABASE_NAME = "todo_database"

@Database(entities = [TodoItemDto::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoItemDao(): TodoItemDao

    companion object {
        fun getDatabase(context: Context): TodoDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                TodoDatabase::class.java,
                TODO_DATABASE_NAME
            ).build()
        }
    }

}
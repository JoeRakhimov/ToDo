package com.joerakhimov.todo.data.source.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joerakhimov.todo.data.dto.TodoItemDto

@Dao
interface TodoItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TodoItemDto>)

    @Query("SELECT * FROM todo_items")
    suspend fun getAllTodoItems(): List<TodoItemDto>

    @Query("DELETE FROM todo_items") // Adjust to match your table name
    suspend fun deleteAll()

}
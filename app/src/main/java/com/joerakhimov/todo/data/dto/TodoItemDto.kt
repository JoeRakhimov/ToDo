package com.joerakhimov.todo.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "todo_items")
data class TodoItemDto(

	@PrimaryKey
	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("last_updated_by")
	val lastUpdatedBy: String = "",

	@field:SerializedName("color")
	val color: String? = null,

	@field:SerializedName("importance")
	val importance: String,

	@field:SerializedName("created_at")
	val createdAt: Long,

	@field:SerializedName("changed_at")
	val changedAt: Long? = null,

	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("deadline")
	val deadline: Long? = null,

	@field:SerializedName("done")
	val done: Boolean

)
package com.joerakhimov.todo.data.api

import com.google.gson.annotations.SerializedName

data class TodoItemDto(

	@field:SerializedName("last_updated_by")
	val lastUpdatedBy: String? = null,

	@field:SerializedName("color")
	val color: String? = null,

	@field:SerializedName("importance")
	val importance: String,

	@field:SerializedName("created_at")
	val createdAt: Long,

	@field:SerializedName("files")
	val files: Any? = null,

	@field:SerializedName("changed_at")
	val changedAt: Long? = null,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("deadline")
	val deadline: Long? = null,

	@field:SerializedName("done")
	val done: Boolean

)
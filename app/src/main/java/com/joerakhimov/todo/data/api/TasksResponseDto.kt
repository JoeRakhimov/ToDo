package com.joerakhimov.todo.data.api

import com.google.gson.annotations.SerializedName

data class TasksResponseDto(

	@field:SerializedName("list")
	val list: List<TodoItemDto>,

	@field:SerializedName("revision")
	val revision: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)

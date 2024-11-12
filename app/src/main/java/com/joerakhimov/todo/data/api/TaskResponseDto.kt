package com.joerakhimov.todo.data.api

import com.google.gson.annotations.SerializedName

data class TaskResponseDto(

	@field:SerializedName("element")
	val element: TodoItemDto,

	@field:SerializedName("revision")
	val revision: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)

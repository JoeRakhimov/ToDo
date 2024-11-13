package com.joerakhimov.todo.data.dto

import com.google.gson.annotations.SerializedName

data class TodoResponseDto(

	@field:SerializedName("element")
	val element: TodoItemDto,

	@field:SerializedName("revision")
	val revision: Int,

	@field:SerializedName("status")
	val status: String? = null
)

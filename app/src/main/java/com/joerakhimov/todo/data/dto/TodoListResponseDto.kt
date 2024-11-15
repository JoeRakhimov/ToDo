package com.joerakhimov.todo.data.dto

import com.google.gson.annotations.SerializedName

data class TodoListResponseDto(

	@field:SerializedName("list")
	val list: List<TodoItemDto>,

	@field:SerializedName("revision")
	val revision: Int? = null,

	@field:SerializedName("status")
	val status: String? = null

)

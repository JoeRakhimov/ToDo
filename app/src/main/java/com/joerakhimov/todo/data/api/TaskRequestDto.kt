package com.joerakhimov.todo.data.api

import com.google.gson.annotations.SerializedName

data class TaskRequestDto(

	@field:SerializedName("element")
	val todoItem: TodoItemDto

)

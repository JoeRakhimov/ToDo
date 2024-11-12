package com.joerakhimov.todo.data.dto

import com.google.gson.annotations.SerializedName

data class TaskRequestDto(

	@field:SerializedName("element")
	val todoItem: TodoItemDto

)

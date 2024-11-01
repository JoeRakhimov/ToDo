package com.joerakhimov.todo

import java.util.Date

class TodoItemsRepository {

    private val todoItems = mutableListOf(
        TodoItem("todo_1", "Buy groceries", Importance.NORMAL, null, false, Date(), null),
        TodoItem("todo_2", "Finish Kotlin project", Importance.URGENT, Date(System.currentTimeMillis() + 86400000), false, Date(), null),
        TodoItem("todo_3", "Call mom", Importance.NORMAL, null, true, Date(), Date()),
        TodoItem("todo_4", "Schedule dentist appointment", Importance.URGENT, Date(System.currentTimeMillis() + 604800000), false, Date(), null),
        TodoItem("todo_5", "Read a book", Importance.LOW, null, false, Date(), null),
        TodoItem("todo_6", "Exercise for 30 minutes", Importance.NORMAL, null, true, Date(), Date()),
        TodoItem("todo_7", "Prepare presentation for work", Importance.URGENT, Date(System.currentTimeMillis() + 432000000), false, Date(), null),
        TodoItem("todo_8", "Clean the house", Importance.NORMAL, null, false, Date(), null),
        TodoItem("todo_9", "Water the plants", Importance.LOW, null, true, Date(), Date()),
        TodoItem("todo_10", "Finish the report", Importance.URGENT, Date(System.currentTimeMillis() + 86400000), false, Date(), null),
        TodoItem("todo_11", "Buy new shoes", Importance.NORMAL, null, false, Date(), null),
        TodoItem("todo_12", "Plan weekend trip", Importance.NORMAL, Date(System.currentTimeMillis() + 259200000), false, Date(), null),
        TodoItem("todo_13", "Check emails", Importance.LOW, null, true, Date(), Date()),
        TodoItem("todo_14", "Organize the closet", Importance.NORMAL, null, false, Date(), null),
        TodoItem("todo_15", "Attend yoga class", Importance.NORMAL, Date(System.currentTimeMillis() + 86400000), false, Date(), null),
        TodoItem("todo_16", "Grocery shopping for next week", Importance.NORMAL, Date(System.currentTimeMillis() + 604800000), false, Date(), null),
        TodoItem("todo_17", "Prepare dinner", Importance.NORMAL, null, false, Date(), null),
        TodoItem("todo_18", "Finish online course", Importance.URGENT, Date(System.currentTimeMillis() + 432000000), false, Date(), null),
        TodoItem("todo_19", "Clean the car", Importance.LOW, null, true, Date(), Date()),
        TodoItem("todo_20", "Buy birthday gift", Importance.URGENT, Date(System.currentTimeMillis() + 604800000), false, Date(), null)
    )

    fun getTodoItems(): List<TodoItem> {
        return todoItems
    }

    fun addTodoItem(todoItem: TodoItem) {
        todoItems.add(todoItem)
    }

    fun deleteTodoItem(todoItem: TodoItem) {
        todoItems.remove(todoItem)
    }

}
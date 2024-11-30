package com.example.finalsimpletodo.data.model

data class TodoItem(
    val id: String = "",
    val title: String = "",
    val completed: Boolean = false,
    val userId: String = ""
)
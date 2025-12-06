package com.example.appquanlycv.data

data class Task(
    val id: Long = 0L,
    val title: String,
    val description: String,
    val dueDateTime: Long,
    val isCompleted: Boolean,
    val reminderEnabled: Boolean
)

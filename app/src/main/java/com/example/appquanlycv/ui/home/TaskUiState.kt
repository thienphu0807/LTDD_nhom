package com.example.appquanlycv.ui.home

import com.example.appquanlycv.data.Task
import java.time.LocalDate

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val tasksForSelectedDate: List<Task> = emptyList(),
    val daysWithTasksInWeek: Int = 0,
    val daysWithTasksInMonth: Int = 0
)

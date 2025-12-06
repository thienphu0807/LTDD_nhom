package com.example.appquanlycv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appquanlycv.data.TaskRepository
import com.example.appquanlycv.reminder.TaskReminderScheduler

class TaskViewModelFactory(
    private val repository: TaskRepository,
    private val reminderScheduler: TaskReminderScheduler
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(repository, reminderScheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

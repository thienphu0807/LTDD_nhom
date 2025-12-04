package com.example.appquanlycv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appquanlycv.data.Task
import com.example.appquanlycv.data.TaskRepository
import com.example.appquanlycv.reminder.TaskReminderScheduler
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository,
    private val reminderScheduler: TaskReminderScheduler
) : ViewModel() {

    private val zoneId: ZoneId = ZoneId.systemDefault()

    private val selectedDate = MutableStateFlow(LocalDate.now())
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState = _uiState.asStateFlow()

    private var scheduledReminderIds: Set<Long> = emptySet()

    init {
        viewModelScope.launch {
            combine(repository.tasks, selectedDate) { tasks, selectedDate ->
                val sortedTasks = tasks.sortedBy { it.dueDateTime }
                val tasksForDate = sortedTasks.filter { it.toLocalDate() == selectedDate }

                val weekStart = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val weekEnd = selectedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                val monthStart = selectedDate.withDayOfMonth(1)
                val monthEnd = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth())

                TaskUiState(
                    tasks = sortedTasks,
                    selectedDate = selectedDate,
                    tasksForSelectedDate = tasksForDate,
                    daysWithTasksInWeek = countDaysWithTasks(sortedTasks, weekStart, weekEnd),
                    daysWithTasksInMonth = countDaysWithTasks(sortedTasks, monthStart, monthEnd)
                )
            }.collect { state ->
                _uiState.value = state
                syncReminders(state.tasks)
            }
        }
    }

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
    }

    fun addTask(
        title: String,
        description: String,
        dueDateTime: Long,
        reminderEnabled: Boolean
    ) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) return

        val task = Task(
            title = trimmedTitle,
            description = description.trim(),
            dueDateTime = dueDateTime,
            isCompleted = false,
            reminderEnabled = reminderEnabled
        )

        viewModelScope.launch {
            val newId = repository.addTask(task)
            val savedTask = task.copy(id = newId)
            if (reminderEnabled) {
                reminderScheduler.schedule(savedTask)
            }
        }
    }

    fun toggleCompletion(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = isCompleted)
            repository.updateTask(updated)
            if (isCompleted) {
                reminderScheduler.cancel(task.id)
            } else if (updated.reminderEnabled) {
                reminderScheduler.schedule(updated)
            }
        }
    }

    fun toggleReminder(task: Task, enabled: Boolean) {
        viewModelScope.launch {
            val updated = task.copy(reminderEnabled = enabled)
            repository.updateTask(updated)
            if (enabled) {
                reminderScheduler.schedule(updated)
            } else {
                reminderScheduler.cancel(task.id)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            reminderScheduler.cancel(task.id)
        }
    }

    private fun syncReminders(tasks: List<Task>) {
        val activeTasks = tasks.filter {
            it.reminderEnabled && !it.isCompleted && it.dueDateTime > System.currentTimeMillis()
        }
        val activeIds = activeTasks.map { it.id }.toSet()
        val toSchedule = activeTasks.filter { it.id !in scheduledReminderIds }
        val toCancel = scheduledReminderIds - activeIds

        toSchedule.forEach { reminderScheduler.schedule(it) }
        toCancel.forEach { reminderScheduler.cancel(it) }

        scheduledReminderIds = activeIds
    }

    private fun countDaysWithTasks(tasks: List<Task>, start: LocalDate, end: LocalDate): Int {
        return tasks.map { it.toLocalDate() }
            .filter { !it.isBefore(start) && !it.isAfter(end) }
            .toSet()
            .size
    }

    private fun Task.toLocalDate(): LocalDate {
        return Instant.ofEpochMilli(dueDateTime).atZone(zoneId).toLocalDate()
    }
}


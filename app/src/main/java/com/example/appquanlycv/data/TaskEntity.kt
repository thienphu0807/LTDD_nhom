package com.example.appquanlycv.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String,
    @ColumnInfo(name = "due_date_time")
    val dueDateTime: Long,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean,
    @ColumnInfo(name = "reminder_enabled")
    val reminderEnabled: Boolean
)

fun TaskEntity.toTask(): Task = Task(
    id = id,
    title = title,
    description = description,
    dueDateTime = dueDateTime,
    isCompleted = isCompleted,
    reminderEnabled = reminderEnabled
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    dueDateTime = dueDateTime,
    isCompleted = isCompleted,
    reminderEnabled = reminderEnabled
)

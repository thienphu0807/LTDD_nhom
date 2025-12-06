package com.example.appquanlycv.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val taskDao: TaskDao) {
    val tasks: Flow<List<Task>> = taskDao.getTasks().map { entities ->
        entities.map { it.toTask() }
    }

    suspend fun addTask(task: Task): Long {
        return taskDao.insert(task.toEntity())
    }

    suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity())
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task.toEntity())
    }
}

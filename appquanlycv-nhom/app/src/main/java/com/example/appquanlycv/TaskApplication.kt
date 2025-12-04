package com.example.appquanlycv

import android.app.Application
import androidx.room.Room
import com.example.appquanlycv.data.TaskDatabase
import com.example.appquanlycv.data.TaskRepository

class TaskApplication : Application() {
    val database: TaskDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java,
            "tasks.db"
        )
            .fallbackToDestructiveMigration()     // 🔥 THÊM DÒNG NÀY
            .build()

    }

    val repository: TaskRepository by lazy {
        TaskRepository(database.taskDao())
    }
}

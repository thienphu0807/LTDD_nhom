package com.example.appquanlycv.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.appquanlycv.data.Task

class TaskReminderScheduler(private val context: Context) {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(task: Task) {
        if (task.id <= 0L) return
        if (!task.reminderEnabled || task.isCompleted) return
        val triggerAtMillis = task.dueDateTime
        if (triggerAtMillis <= System.currentTimeMillis()) return

        cancel(task.id)

        val pendingIntent = createPendingIntent(task)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    fun cancel(taskId: Long) {
        val pendingIntent = createPendingIntent(taskId)
        alarmManager.cancel(pendingIntent)
    }

    private fun createPendingIntent(task: Task): PendingIntent {
        return createPendingIntent(task.id, task.title, task.description)
    }

    private fun createPendingIntent(
        taskId: Long,
        title: String? = null,
        description: String? = null
    ): PendingIntent {
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra(TaskReminderReceiver.EXTRA_TASK_ID, taskId)
            putExtra(TaskReminderReceiver.EXTRA_TASK_TITLE, title)
            putExtra(TaskReminderReceiver.EXTRA_TASK_DESCRIPTION, description)
        }

        return PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

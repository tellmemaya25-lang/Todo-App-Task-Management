package com.sabihon.todo.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * WorkManager worker that fires task reminder notification.
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getString("taskId") ?: return Result.failure()
        val taskTitle = inputData.getString("taskTitle") ?: "Task Reminder"
        val taskDescription = inputData.getString("taskDescription") ?: ""

        val notification = notificationHelper.buildTaskReminderNotification(
            taskId = taskId,
            taskTitle = taskTitle,
            taskDescription = taskDescription
        )

        try {
            NotificationManagerCompat.from(applicationContext).notify(taskId.hashCode(), notification)
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission not granted
            return Result.failure()
        }

        return Result.success()
    }
}

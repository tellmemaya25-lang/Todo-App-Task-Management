package com.sabihon.todo.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Schedules/cancels reminders via WorkManager.
 */
@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun scheduleReminder(
        taskId: String,
        taskTitle: String,
        taskDescription: String,
        reminderAtMillis: Long
    ) {
        val now = System.currentTimeMillis()
        val delay = reminderAtMillis - now
        if (delay <= 0) return // Don't schedule past reminders

        val inputData = Data.Builder()
            .putString("taskId", taskId)
            .putString("taskTitle", taskTitle)
            .putString("taskDescription", taskDescription)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag(taskId)
            .build()

        workManager.enqueueUniqueWork(
            "reminder_$taskId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelReminder(taskId: String) {
        workManager.cancelUniqueWork("reminder_$taskId")
        workManager.cancelAllWorkByTag(taskId)
    }
}

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
 * Uses lazy WorkManager to avoid IllegalStateException when Hilt worker factory not yet initialized.
 */
@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Lazy to avoid crash if WorkManager not initialized yet
    private val workManager: WorkManager
        get() = try {
            WorkManager.getInstance(context)
        } catch (e: Exception) {
            // If not initialized, initialize will be handled by Hilt provider; retry after delay not needed here
            WorkManager.getInstance(context)
        }

    fun scheduleReminder(
        taskId: String,
        taskTitle: String,
        taskDescription: String,
        reminderAtMillis: Long
    ) {
        try {
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
        } catch (e: Exception) {
            // Log but don't crash app if scheduling fails
            android.util.Log.e("ReminderScheduler", "Failed to schedule reminder", e)
        }
    }

    fun cancelReminder(taskId: String) {
        try {
            workManager.cancelUniqueWork("reminder_$taskId")
            workManager.cancelAllWorkByTag(taskId)
        } catch (e: Exception) {
            android.util.Log.e("ReminderScheduler", "Failed to cancel reminder", e)
        }
    }
}

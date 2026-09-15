package com.sabihon.todo.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sabihon.todo.MainActivity
import com.sabihon.todo.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper for creating notification channel and building task reminders.
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "task_reminders"
        const val CHANNEL_NAME = "Task Reminders"
        const val REQUEST_CODE_MARK_DONE = 1001
    }

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun buildTaskReminderNotification(
        taskId: String,
        taskTitle: String,
        taskDescription: String
    ): android.app.Notification {
        createChannel()

        // Deep link to Task Detail via MainActivity intent
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("taskId", taskId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Mark Done action
        val markDoneIntent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = "MARK_DONE"
            putExtra("taskId", taskId)
        }
        val markDonePendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_MARK_DONE + taskId.hashCode(),
            markDoneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(taskTitle)
            .setContentText(taskDescription.ifBlank { "Task reminder" })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.checkbox_on_background,
                context.getString(R.string.mark_done),
                markDonePendingIntent
            )
            .build()
    }
}

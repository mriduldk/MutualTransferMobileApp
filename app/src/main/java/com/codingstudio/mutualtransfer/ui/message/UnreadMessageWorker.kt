package com.codingstudio.mutualtransfer.ui.message

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.SharedPref
import java.util.Calendar

class UnreadMessageWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val hasUnread = SharedPref().getBooleanPref(applicationContext, Constants.hasUnReadMessage)

        if (hasUnread) {

            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)

            // Only notify between 8 AM and 10 PM
            if (hour in 8..21) {
                showNotification("Unread Message", "You have unread messages. Check them now!")
            }

            //Log.e("UnreadMessageWorker", "Hour: $hour")
            //showNotification("Unread Message", "You have unread messages. Check them now!")
        }

        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "UNREAD_MESSAGE_CHANNEL"
        val notificationId = 102

        // Create intent to open MessageActivity
        val intent = Intent(applicationContext, MessageActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Unread Message Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for unread message alerts"
            }

            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationId, builder.build())
            }
        }
    }
}

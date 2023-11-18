package com.example.funchat

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.funchat.R.drawable.envelope
import com.example.funchat.R.drawable.notificationlogo
import com.example.funchat.R.drawable.notificationlogo1
import com.example.funchat.R.drawable.notoficationlogo

class NotificationHelper(private val context: Context) {

    init {
        // Ensure notification channel for Android Oreo and higher
        createNotificationChannel()
    }

    // Function to create and show a notification with a custom icon
    fun showNotification(messageTitle: String?, messageBody: String?) {
        // Set the custom icon for the notification
        val customIcon = notificationlogo1

        // Build the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(customIcon)
            .setColor(ContextCompat.getColor(context,R.color.applogo))// Set your custom icon here
            .setContentTitle("Welcome to FunChat")
            .setContentText("Testing notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    // Function to create a notification channel for Android Oreo and higher
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "message",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Your Channel Description"
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        // Constants
        const val CHANNEL_ID = "message"
        const val NOTIFICATION_ID = 1
    }
}

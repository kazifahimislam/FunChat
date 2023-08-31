package com.example.funchat

import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMNotificationService : FirebaseMessagingService() {

    private val TAG = "FCMNotificationService"

    private val CHANNEL_ID = "your_channel_id" // Define your channel ID
    private val NOTIFICATION_ID = 123 // Define your notification ID

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Handle the new FCM token here, you can send it to your server if needed
        Log.d(TAG, "Refreshed FCM Token: $token")
    }
}

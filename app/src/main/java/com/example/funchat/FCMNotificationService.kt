package com.example.funchat

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMNotificationService : FirebaseMessagingService() {

    private val TAG = "FCMNotificationService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle the incoming FCM message here
        remoteMessage.notification?.let { notification ->
            val title = notification.title
            val body = notification.body

            // You can now use the title and body to display the notification
            // using the NotificationManager or any other method you prefer.
            Log.d(TAG, "Received FCM Notification - Title: $title, Body: $body")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Handle the new FCM token here, you can send it to your server if needed
        Log.d(TAG, "Refreshed FCM Token: $token")
    }
}

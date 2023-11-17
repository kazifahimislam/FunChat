package com.example.funchat

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Handle the received message

        val messageTitle = remoteMessage.notification?.title
        val messageBody = remoteMessage.notification?.body

        // Create an instance of NotificationHelper
        val notificationHelper = NotificationHelper(applicationContext)

        // Call the showNotification function with message details
        notificationHelper.showNotification(messageTitle, messageBody)
    }
}

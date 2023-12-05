//MyFirebaseMessagingService

package com.example.firebasetest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // Get updated InstanceID token.
        Log.d(TAG, "Refreshed token: $token")

        // TODO: Implement this method to send any registration to your app's servers.
        // sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "Message data payload: ${remoteMessage.data}")

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")

            // Handle notification
            sendNotification(it.title, it.body)
        }
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val channelId = "my_channel_id"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)

            .setContentTitle(title)
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager =
            getSystemService(NotificationManager::class.java)

        // Since Android Oreo, notification channel is required.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        const val TAG = "MyFirebaseMessaging"
    }
}

package com.example.myfirstapp.components.network

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.SystemClock

class NetworkChangeReceiver : BroadcastReceiver() {

    companion object {
        private var lastNotificationTime: Long = 0
        private const val NOTIFICATION_INTERVAL = 4 * 60 * 60 * 1000 // 4 hours in milliseconds
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (isConnected(context)) {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastNotificationTime > NOTIFICATION_INTERVAL) {
                sendNotification(context)
                lastNotificationTime = currentTime
            }
        }
    }

    private fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun sendNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a simple notification
        val notification = Notification.Builder(context)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Default Android icon
            .setContentTitle("Internet Restored!")
            .setContentText("Catch up on what's happening now!")
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}

package com.example.myfirstapp.components.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.myfirstapp.DinosaurGame

class NetworkChangeReceiver(private val dinosaurGame: DinosaurGame?) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            dinosaurGame?.onNetworkAvailable() // Notify the DinosaurGame that the network is available
        }
    }
}

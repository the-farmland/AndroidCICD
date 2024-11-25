package com.example.myfirstapp.components.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.myfirstapp.DinosaurGame

class NetworkChangeReceiver : BroadcastReceiver() {

    private var dinosaurGame: DinosaurGame? = null

    // Default constructor required for BroadcastReceiver
    constructor()

    // Optionally, you could have a constructor that accepts DinosaurGame for initialization
    constructor(dinosaurGame: DinosaurGame?) : this() {
        this.dinosaurGame = dinosaurGame
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            dinosaurGame?.onNetworkAvailable() // Notify DinosaurGame that the network is available
        }
    }

    // Method to update the DinosaurGame instance dynamically if needed
    fun setDinosaurGame(dinosaurGame: DinosaurGame?) {
        this.dinosaurGame = dinosaurGame
    }
}

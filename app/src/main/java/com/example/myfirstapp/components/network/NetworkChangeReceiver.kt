package com.example.myfirstapp.components.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.widget.Toast
import com.example.myfirstapp.DinosaurGame

class NetworkChangeReceiver : BroadcastReceiver() {

    private var dinosaurGame: DinosaurGame? = null

    // Setter method to pass the DinosaurGame instance
    fun setDinosaurGame(game: DinosaurGame) {
        this.dinosaurGame = game
    }

    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork

        // Check for network capabilities
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        
        if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            // Network is available
            dinosaurGame?.onNetworkAvailable() // Notify the DinosaurGame
        } else {
            // Network is not available
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to update the DinosaurGame instance dynamically if needed
    fun setDinosaurGame(dinosaurGame: DinosaurGame?) {
        this.dinosaurGame = dinosaurGame
    }
}

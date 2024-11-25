package com.example.myfirstapp.components.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast
import com.example.myfirstapp.DinosaurGame

class NetworkChangeReceiver : BroadcastReceiver() {

<<<<<<< HEAD
    private var dinosaurGame: DinosaurGame? = null

    // Setter method to pass the DinosaurGame instance
    fun setDinosaurGame(game: DinosaurGame) {
        this.dinosaurGame = game
    }

    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo

        if (activeNetwork != null && activeNetwork.isConnected) {
            // Network is available
            dinosaurGame?.onNetworkAvailable() // Notify the DinosaurGame
        } else {
            // Network is not available
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
        if (networkInfo != null && networkInfo.isConnected) {
            dinosaurGame?.onNetworkAvailable() // Notify DinosaurGame that the network is available
>>>>>>> 86a9fe19ce68bc67f97d5a72449af1b494d481ff
        }
    }

    // Method to update the DinosaurGame instance dynamically if needed
    fun setDinosaurGame(dinosaurGame: DinosaurGame?) {
        this.dinosaurGame = dinosaurGame
    }
}

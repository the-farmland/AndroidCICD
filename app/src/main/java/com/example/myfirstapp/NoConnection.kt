package com.example.myfirstapp

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView

class NoConnection(private val context: Context) {

    private var dinosaurGame: DinosaurGame? = null
    private lateinit var networkChangeReceiver: NetworkChangeReceiver

    init {
        // Initialize and register the receiver when the NoConnection class is created
        networkChangeReceiver = NetworkChangeReceiver(dinosaurGame)
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkChangeReceiver, intentFilter)
    }

    fun handleNoConnection(
        webView: android.webkit.WebView,
        container: FrameLayout,
        tryAgainButton: Button,
        errorMessage: android.widget.TextView,
        url: String
    ) {
        if (isNetworkAvailable()) {
            // Internet is available
            showReconnectionMessage(container, webView)
            webView.loadUrl(url)
            webView.visibility = View.VISIBLE
            errorMessage.visibility = View.GONE
            tryAgainButton.visibility = View.GONE

            // Remove dinosaur game if it exists
            dinosaurGame?.let {
                it.stopGame()
                container.removeView(it)
                dinosaurGame = null
            }
        } else {
            // No internet connection
            webView.visibility = View.GONE
            errorMessage.visibility = View.GONE
            tryAgainButton.visibility = View.GONE

            // Show dinosaur game
            if (dinosaurGame == null) {
                dinosaurGame = DinosaurGame(context)
                container.addView(dinosaurGame, 0)
            }
            dinosaurGame?.visibility = View.VISIBLE
        }

        tryAgainButton.setOnClickListener {
            handleNoConnection(webView, container, tryAgainButton, errorMessage, url)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showReconnectionMessage(container: FrameLayout, webView: android.webkit.WebView) {
        dinosaurGame?.onNetworkAvailable()
        
        // Hide the message after 3 seconds
        Handler().postDelayed({
            dinosaurGame?.onNetworkAvailable()
            webView.loadUrl("https://www.plus-us.com")
        }, 3000)
    }

    // Ensure the receiver is unregistered when it's no longer needed
    fun unregisterReceiver() {
        try {
            context.unregisterReceiver(networkChangeReceiver)
        } catch (e: Exception) {
            // Log or handle the exception if necessary
        }
    }

    // Define the NetworkChangeReceiver here
    private class NetworkChangeReceiver(private val dinosaurGame: DinosaurGame?) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

            if (networkInfo != null && networkInfo.isConnected) {
                dinosaurGame?.onNetworkAvailable() // Notify the DinosaurGame that the network is available
            }
        }
    }
}

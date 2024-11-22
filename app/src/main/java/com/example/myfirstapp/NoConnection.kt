package com.example.myfirstapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import android.webkit.WebView
import android.webkit.WebViewClient

class NoConnection(private val context: Context) {

    // This function is called when there is no internet or page fails to load
    fun handleNoConnection(
        webView: WebView, 
        tryAgainButton: Button, 
        errorMessage: TextView, 
        url: String
    ) {
        // Check if the device is connected to the internet
        if (isNetworkAvailable()) {
            // If connected, load the webpage
            webView.loadUrl(url)
            errorMessage.visibility = View.GONE
            tryAgainButton.visibility = View.GONE
        } else {
            // If no internet, show the error message and "Try Again" button
            errorMessage.text = "No internet connection. Please try again."
            errorMessage.visibility = View.VISIBLE
            tryAgainButton.visibility = View.VISIBLE
        }

        // Set up the "Try Again" button to reload the page when clicked
        tryAgainButton.setOnClickListener {
            handleNoConnection(webView, tryAgainButton, errorMessage, url)
        }
    }

    // Check if the device has an active network connection
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}

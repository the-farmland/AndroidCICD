package com.example.myfirstapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.webkit.WebView

class NoConnection(private val context: Context) {
    private var dinosaurGame: DinosaurGame? = null
    
    fun handleNoConnection(
        webView: WebView,
        container: FrameLayout,
        tryAgainButton: Button,
        errorMessage: TextView,
        url: String
    ) {
        if (isNetworkAvailable()) {
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
            webView.visibility = View.GONE
            errorMessage.visibility = View.VISIBLE
            tryAgainButton.visibility = View.VISIBLE
            
            // Show dinosaur game
            if (dinosaurGame == null) {
                dinosaurGame = DinosaurGame(context)
                container.addView(dinosaurGame)
            }
        }

        tryAgainButton.setOnClickListener {
            handleNoConnection(webView, container, tryAgainButton, errorMessage, url)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
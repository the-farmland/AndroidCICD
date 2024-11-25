package com.example.myfirstapp

import android.content.Context
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView

class NoConnection(private val context: Context) {
    private var dinosaurGame: DinosaurGame? = null

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
        dinosaurGame?.connectionMessage = "Connection re-established!"
        
        // Hide the message after 3 seconds
        Handler().postDelayed({
            dinosaurGame?.connectionMessage = null
            webView.loadUrl("https://www.plus-us.com")
        }, 3000)
    }
}

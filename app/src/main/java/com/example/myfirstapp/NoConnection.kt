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
        errorMessage: TextView,
        url: String
    ) {
        if (isNetworkAvailable()) {
            // Internet is available
            dinosaurGame?.let { game ->
                // Show connection message over the game
                game.connectionMessage = "Connection Restored\nTap to continue"
                game.onConnectionRestored = {
                    // When message is tapped, restore webview
                    game.stopGame()
                    container.removeView(game)
                    dinosaurGame = null
                    webView.visibility = View.VISIBLE
                    webView.loadUrl(url)
                }
            } ?: run {
                // If no game is running, just restore webview
                webView.visibility = View.VISIBLE
                webView.loadUrl(url)
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
}
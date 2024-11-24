package com.example.myfirstapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import android.view.ViewGroup
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
            // Internet is available
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
            errorMessage.visibility = View.GONE  // Hide error message when showing game
            tryAgainButton.visibility = View.GONE  // Hide try again button when showing game

            // Show dinosaur game
            if (dinosaurGame == null) {
                dinosaurGame = DinosaurGame(context)
                // Set layout parameters for the game to fill the container
                val layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                dinosaurGame?.layoutParams = layoutParams
                container.addView(dinosaurGame, 0)  // Add at index 0 to be behind other views
            }
            dinosaurGame?.visibility = View.VISIBLE
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
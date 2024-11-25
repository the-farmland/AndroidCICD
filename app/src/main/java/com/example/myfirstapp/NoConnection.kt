package com.example.myfirstapp

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

class NoConnection(private val context: Context) {
    private var dinosaurGame: DinosaurGame? = null
    private var popupView: View? = null

    fun handleNoConnection(
        webView: android.webkit.WebView,
        container: FrameLayout,
        tryAgainButton: Button,
        errorMessage: android.widget.TextView,
        url: String
    ) {
        if (isNetworkAvailable()) {
            // Internet is available
            showReconnectionPopup(container)
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
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
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
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showReconnectionPopup(container: FrameLayout) {
        // Remove any existing popup to avoid duplicates
        popupView?.let { container.removeView(it) }

        // Create the popup programmatically
        popupView = LinearLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
                setMargins(32, 32, 32, 32)
            }
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 20)
            setBackgroundColor(Color.WHITE)
            elevation = 10f
        }

        // Add text to the popup
        val message = TextView(context).apply {
            text = "Connection re-established!"
            textSize = 18f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }

        // Add a "Dismiss" button
        val dismissButton = Button(context).apply {
            text = "Dismiss"
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_light))
            setOnClickListener {
                popupView?.let { container.removeView(it) }
                popupView = null
            }
        }

        (popupView as LinearLayout).addView(message)
        (popupView as LinearLayout).addView(dismissButton)

        container.addView(popupView)

        // Auto-dismiss popup after 3 seconds
        Handler().postDelayed({
            popupView?.let { container.removeView(it) }
            popupView = null
        }, 3000)
    }
}

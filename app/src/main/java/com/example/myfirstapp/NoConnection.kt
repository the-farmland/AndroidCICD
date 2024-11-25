package com.example.myfirstapp

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView

class NoConnection(private val context: Context) {
    private var dinosaurGame: DinosaurGame? = null
    private var reconnectionPopup: TextView? = null

    fun handleNoConnection(
        webView: android.webkit.WebView,
        container: FrameLayout,
        tryAgainButton: Button,
        errorMessage: TextView,
        url: String
    ) {
        if (isNetworkAvailable()) {
            // Internet is available
            showReconnectionMessage(container, webView, url)
            dinosaurGame?.let {
                // Keep the game visible but show message over it
                showGameOverlay(container, url, webView)
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

    private fun showGameOverlay(container: FrameLayout, url: String, webView: android.webkit.WebView) {
        // Remove existing popup if any
        reconnectionPopup?.let { container.removeView(it) }

        reconnectionPopup = TextView(context).apply {
            text = "Connection Restored\nTap to continue"
            setTextColor(Color.WHITE)
            textSize = 20f
            gravity = Gravity.CENTER
            setPadding(40, 20, 40, 20)
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.parseColor("#007AFF"))
                cornerRadius = 25f
                alpha = 230 // Slightly transparent
            }

            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }

            setOnClickListener {
                // Remove game and popup
                dinosaurGame?.let {
                    it.stopGame()
                    container.removeView(it)
                    dinosaurGame = null
                }
                container.removeView(this)
                webView.visibility = View.VISIBLE
                webView.loadUrl(url)
            }
        }

        // Add popup to container (on top of the game)
        container.addView(reconnectionPopup)

        // Fade in animation
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 500
            fillAfter = true
        }
        reconnectionPopup?.startAnimation(fadeIn)
    }

    private fun showReconnectionMessage(container: FrameLayout, webView: android.webkit.WebView, url: String) {
        // Keep the game running and show overlay
        if (dinosaurGame != null) {
            showGameOverlay(container, url, webView)
        } else {
            // If no game is running, just restore the webview
            webView.visibility = View.VISIBLE
            webView.loadUrl(url)
        }
    }
}
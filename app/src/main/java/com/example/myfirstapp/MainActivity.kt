package com.example.myfirstapp

import android.os.Bundle
import android.os.Build
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.view.Gravity
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the WebView
        val webView = WebView(this)

        // Create a FrameLayout to hold both WebView and the "Try Again" message
        val layout = FrameLayout(this)
        setContentView(layout)

        // Create a "Try Again" button and set its visibility to GONE initially
        val tryAgainButton = Button(this)
        tryAgainButton.text = "Try Again"
        tryAgainButton.gravity = Gravity.CENTER
        tryAgainButton.visibility = View.GONE

        // Create a TextView for the error message
        val errorMessage = TextView(this)
        errorMessage.text = "No internet connection. Please try again."
        errorMessage.gravity = Gravity.CENTER
        errorMessage.visibility = View.GONE

        // Add WebView, error message, and "Try Again" button to the layout
        layout.addView(webView)
        layout.addView(errorMessage)
        layout.addView(tryAgainButton)

        // Enable WebView debugging only for devices with API level 19 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        // Configure WebView settings
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("WebView", "Page started loading: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WebView", "Page finished loading: $url")
            }

            override fun onReceivedError(
                view: WebView?, errorCode: Int, description: String?, failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e("WebViewError", "Error loading URL: $failingUrl, Description: $description")
                // Show error message and retry button if page fails to load
                val noConnection = NoConnection(this@MainActivity)
                noConnection.handleNoConnection(webView, tryAgainButton, errorMessage, "https://www.plus-us.com")
            }

            override fun onReceivedHttpError(
                view: WebView?, request: WebResourceRequest?, errorResponse: android.webkit.WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                Log.e("WebViewHttpError", "HTTP error loading URL: ${request?.url}")
            }
        }

// Enable JavaScript and other settings required for modern web applications
         webView.settings.javaScriptEnabled = true  
         webView.settings.domStorageEnabled = true 
         webView.settings.allowFileAccess = true
         webView.settings.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT


        // Load the desired webpage
        val noConnection = NoConnection(this)
        noConnection.handleNoConnection(webView, tryAgainButton, errorMessage, "https://www.plus-us.com")
    }
}

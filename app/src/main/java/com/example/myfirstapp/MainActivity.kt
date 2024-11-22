package com.example.myfirstapp

import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create and set up the WebView
        val webView = WebView(this)
        setContentView(webView)

        // Enable debugging for WebView
        WebView.setWebContentsDebuggingEnabled(true)

        // Configure WebView settings
        webView.settings.javaScriptEnabled = true // Enable JavaScript if required
        webView.settings.domStorageEnabled = true  // Enable DOM storage
        webView.settings.setAppCacheEnabled(true)  // Enable app cache
        webView.settings.allowFileAccess = true    // Allow file access if necessary
        webView.settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW // For mixed content

        // Custom WebViewClient to capture errors and network requests
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?, errorCode: Int, description: String?, failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e("WebViewError", "Error loading URL: $failingUrl, Description: $description")
            }

            override fun onReceivedHttpError(
                view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                Log.e("WebViewHttpError", "HTTP error loading URL: ${request?.url}")
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("WebView", "Page started loading: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WebView", "Page finished loading: $url")
            }
        }

        // Load the desired webpage
        webView.loadUrl("https://www.plus-us.com") // Replace with your desired URL
    }
}

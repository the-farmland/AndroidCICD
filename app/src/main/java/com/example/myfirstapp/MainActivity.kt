package com.example.myfirstapp

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create and set up the WebView
        val webView = WebView(this)
        setContentView(webView)

        // Configure WebView settings
        webView.webViewClient = WebViewClient() // Ensures links open in the WebView
        webView.settings.javaScriptEnabled = true // Enable JavaScript if required

        // Load the desired webpage
        webView.loadUrl("https://www.example.com") // Replace with your desired URL
    }
}

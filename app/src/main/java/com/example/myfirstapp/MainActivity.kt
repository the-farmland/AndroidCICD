package com.example.myfirstapp

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a WebView instance
        val webView = WebView(this)
        setContentView(webView)

        // Configure the WebView
        webView.webViewClient = WebViewClient() // Ensures links open within the app
        webView.settings.javaScriptEnabled = true // Enable JavaScript if needed

        // Load a URL
        webView.loadUrl("https://www.example.com") // Replace with your desired URL
    }
}

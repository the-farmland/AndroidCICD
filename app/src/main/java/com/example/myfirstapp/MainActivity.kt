package com.example.myfirstapp

import android.os.Bundle
import android.os.Build
import android.os.Handler
import android.os.Looper
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
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var doubleBackToExitPressedOnce = false
    private var uploadMessage: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the WebView
        webView = WebView(this)

        // Create a FrameLayout to hold both WebView and the "Try Again" message
        val layout = FrameLayout(this)
        setContentView(layout)

        // Create a "Try Again" button and set its visibility to GONE initially
        val tryAgainButton = Button(this).apply {
            text = "Try Again"
            gravity = Gravity.CENTER
            visibility = View.GONE
        }

        // Create a TextView for the error message
        val errorMessage = TextView(this).apply {
            text = "No internet connection. Please try again."
            gravity = Gravity.CENTER
            visibility = View.GONE
        }

        // Add WebView, error message, and "Try Again" button to the layout
        layout.addView(webView)
        layout.addView(errorMessage)
        layout.addView(tryAgainButton)

        // Configure WebView settings
        webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT

            // Handle file uploads for versions above API 21 (Lollipop)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webChromeClient = object : WebChromeClient() {
                    override fun onShowFileChooser(
                        view: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: WebChromeClient.FileChooserParams?
                    ): Boolean {
                        uploadMessage = filePathCallback
                        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "image/*"
                            addCategory(Intent.CATEGORY_OPENABLE)
                        }
                        startActivityForResult(intent, 1)
                        return true
                    }
                }
            }

            webViewClient = object : WebViewClient() {
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
                    showError(tryAgainButton, errorMessage)
                }

                override fun onReceivedHttpError(
                    view: WebView?, request: WebResourceRequest?, errorResponse: android.webkit.WebResourceResponse?
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    Log.e("WebViewHttpError", "HTTP error loading URL: ${request?.url}")
                }
            }
        }

        // Set the Try Again button action
        tryAgainButton.setOnClickListener {
            webView.reload()
            hideError(tryAgainButton, errorMessage)
        }

        // Load the initial URL
        loadWebPage("https://www.plus-us.com")
    }

    private fun loadWebPage(url: String) {
        webView.loadUrl(url)
    }

    private fun showError(button: Button, errorMessage: TextView) {
        webView.visibility = View.GONE
        errorMessage.visibility = View.VISIBLE
        button.visibility = View.VISIBLE
    }

    private fun hideError(button: Button, errorMessage: TextView) {
        webView.visibility = View.VISIBLE
        errorMessage.visibility = View.GONE
        button.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            // Navigate back in the WebView history
            webView.goBack()
        } else {
            // Handle double back press to exit
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()

            // Reset the double back press flag after 2 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                uploadMessage?.onReceiveValue(arrayOf(uri))
            }
        } else {
            uploadMessage?.onReceiveValue(null)
        }
    }
}

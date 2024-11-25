package com.example.myfirstapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myfirstapp.components.behaviors.KeyboardBehavior
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var doubleBackToExitPressedOnce = false
    private var uploadMessage: ValueCallback<Array<Uri>>? = null
    private val FILE_PICKER_REQUEST_CODE = 1001
    private lateinit var noConnection: NoConnection
    private lateinit var layout: FrameLayout
    private lateinit var keyboardBehavior: KeyboardBehavior

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make the status bar transparent
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_main)

        // Create the WebView
        webView = WebView(this)

        // Create a FrameLayout to hold both WebView and the "Try Again" message
        layout = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Configure window to adjust resize when keyboard appears
        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        setContentView(layout)

        // Create a "Try Again" button
        val tryAgainButton = Button(this).apply {
            text = "Try Again"
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            visibility = View.GONE
        }

        // Create a TextView for the error message
        val errorMessage = TextView(this).apply {
            text = "No internet connection. Please try again."
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
                setMargins(0, 0, 0, 200)
            }
            visibility = View.GONE
        }

        // Add WebView, error message, and "Try Again" button to the layout
        layout.addView(webView)
        layout.addView(errorMessage)
        layout.addView(tryAgainButton)

        noConnection = NoConnection(this)

        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                cacheMode = WebSettings.LOAD_DEFAULT
            }

            webChromeClient = object : WebChromeClient() {
                override fun onShowFileChooser(
                    view: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?
                ): Boolean {
                    uploadMessage = filePathCallback
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "*/*"
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }
                    startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
                    return true
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

        tryAgainButton.setOnClickListener {
            webView.reload()
            hideError(tryAgainButton, errorMessage)
        }

        // Initialize KeyboardBehavior to manage WebView resizing
        keyboardBehavior = KeyboardBehavior(layout, webView)
        keyboardBehavior.setupKeyboardListener()

        loadWebPage("https://www.plus-us.com")
        requestPermissions()
    }

    private fun loadWebPage(url: String) {
        webView.loadUrl(url)
    }

    private fun showError(button: Button, errorMessage: TextView) {
        noConnection.handleNoConnection(webView, layout, button, errorMessage, webView.url ?: "https://www.plus-us.com")
    }

    private fun hideError(button: Button, errorMessage: TextView) {
        webView.visibility = View.VISIBLE
        errorMessage.visibility = View.GONE
        button.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                MediaPipeline.uploadMedia(uri, this)
                uploadMessage?.onReceiveValue(arrayOf(uri))
            }
        } else {
            uploadMessage?.onReceiveValue(null)
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }
}

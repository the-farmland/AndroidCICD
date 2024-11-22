package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for the splash screen
        setContentView(R.layout.activity_splash)

        // Delay the transition to MainActivity by 2 seconds (2000ms)
        Handler().postDelayed({
            // Start MainActivity after the splash screen
            val intent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(intent)
            finish() // Close SplashActivity so the user can't go back to it
        }, 2000) // 2-second delay (you can adjust this as needed)
    }
}

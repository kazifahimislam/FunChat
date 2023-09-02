package com.example.funchat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_SCREEN_DURATION = 1000 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Delay the execution of other tasks by 1 second
        Handler(Looper.getMainLooper()).postDelayed({
            executeTasks()
        }, SPLASH_SCREEN_DURATION.toLong())
    }

    private fun executeTasks() {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val lastActivity = sharedPref.getString("lastActivity", "Home")

        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        val intent = if (currentUser != null) {
            when (lastActivity) {
                "Home" -> Intent(this, Home::class.java)
                // Add more cases for other activities if needed
                else -> Intent(this, Home::class.java)
            }
        } else {
            Intent(this, GoogleSignUp::class.java)
        }

        startActivity(intent)
        finish()
    }
}

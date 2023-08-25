package com.example.funchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Otp : AppCompatActivity() {
    private lateinit var button : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        button = findViewById(R.id.sendOtp)

        button.setOnClickListener{
            val intent = Intent(this, home::class.java)
            startActivity(intent)
        }
    }
}
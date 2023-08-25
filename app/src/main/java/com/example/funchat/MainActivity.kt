package com.example.funchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var send : Button
    private lateinit var textview : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        send = findViewById(R.id.send)
        textview = findViewById(R.id.newAccount)

        send.setOnClickListener{

        }
        textview.setOnClickListener{
            val intent = Intent(this, Otp::class.java)
            startActivity(intent)
        }
        }



    }

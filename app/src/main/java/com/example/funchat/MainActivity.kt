package com.example.funchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var send : Button
    private lateinit var textview : TextView
    private lateinit var email : EditText
    private lateinit var password : EditText

    private lateinit var mAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        send = findViewById(R.id.send)
        textview = findViewById(R.id.newAccount)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        mAuth = FirebaseAuth.getInstance()

        textview.setOnClickListener{
            val intent = Intent(this, Otp::class.java)
            startActivity(intent)
        }
        send.setOnClickListener{
            val email = email.text.toString()
            val password = password.text.toString()

            login(email,password);

        }


        }

    private fun login (email: String, password : String){

    }

    }

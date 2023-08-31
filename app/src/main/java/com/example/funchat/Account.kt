package com.example.funchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class Account : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)


        auth = FirebaseAuth.getInstance()

        val email = intent.getStringExtra("email")
        val displayName = intent.getStringExtra("name")


        findViewById<TextView>(R.id.txtUserName).text = email + "\n" + displayName

        findViewById<Button>(R.id.signOut).setOnClickListener{
            auth.signOut()
            startActivity(Intent(this, LogInActivity::class.java))
        }
    }
}
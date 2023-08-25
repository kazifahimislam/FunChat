package com.example.funchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class Otp : AppCompatActivity() {
    private lateinit var button : Button
    private lateinit var name : EditText
    private lateinit var email : EditText
    private lateinit var password : EditText

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        button = findViewById(R.id.sendOtp)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        mAuth = FirebaseAuth.getInstance()

        

        button.setOnClickListener{


            val email = email.text.toString()
            val password = password.text.toString()
            val name = name.text.toString()

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {

                        val user = mAuth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { profileTask: Task<Void?> ->
                                if (profileTask.isSuccessful) {
                                    val intent = Intent(this, home::class.java)
                                    startActivity(intent)

                                } else {


                                }
                            }
                    } else {

                    }
                }
        }
    }
}
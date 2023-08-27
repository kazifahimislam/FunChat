package com.example.funchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private lateinit var button : Button
    private lateinit var name : EditText
    private lateinit var email : EditText
    private lateinit var password : EditText

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

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

                        addUserToDatabase(name,email,mAuth.currentUser?.uid!!)
                        val user = mAuth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { profileTask: Task<Void?> ->
                                if (profileTask.isSuccessful) {
                                    val intent = Intent(this, Home::class.java)
                                    finish()
                                    startActivity(intent)

                                } else {


                                }
                            }
                    } else {
                        val text = "Please Enter Proper Details"
                        val duration = Toast.LENGTH_SHORT

                        val toast = Toast.makeText(this, text, duration)
                        toast.show()
                    }
                }
        }
    }
    private fun addUserToDatabase(name: String, email: String, uid: String){

     mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(User(name,email, uid))
    }
}
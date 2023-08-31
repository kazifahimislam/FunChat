package com.example.funchat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        button = findViewById(R.id.sendOtp)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<ImageButton>(R.id.googleAuthentication).setOnClickListener {
            signInGoogle()
        }

        

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
    private fun   signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }
    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if(account != null){
                updateUI(account)

            }

        }
        else
            Toast.makeText(this,task.exception.toString(), Toast.LENGTH_SHORT).show()
    }
    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                val currentUser = mAuth.currentUser
                val uid = currentUser?.uid
                val name = currentUser?.displayName
                val email = currentUser?.email

                // Add user to the database
                if (uid != null && name != null && email != null) {
                    addUserToDatabase(name, email, uid)
                }

                // Start Home activity
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, authTask.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }







}
package com.example.funchat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class GoogleSignUp : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var logIn: TextView
    private lateinit var signUp: Button
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_up)


        logIn = findViewById(R.id.logIn)
        signUp = findViewById(R.id.signUp)

        signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        logIn.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<Button>(R.id.googleAuthentication).setOnClickListener {
            signInGoogle()
        }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String, fcmToken: String , profilePictureUrl: String) {

        mDbRef = FirebaseDatabase.getInstance().getReference()


        mDbRef.child("user").child(uid).setValue(User(name, email, uid , fcmToken , profilePictureUrl))
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)

            }

        } else
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {


                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {

                        return@OnCompleteListener
                    }

                    // Get new FCM registration token
                    val token = task.result

                    val currentUser = mAuth.currentUser
                    val uid = currentUser?.uid
                    val name = currentUser?.displayName
                    val email = currentUser?.email
                    val profilePictureUrl = currentUser?.photoUrl.toString()
                    val fcmToken = token


                    // Add user to the database
                    if (uid != null && name != null && email != null && fcmToken != null && profilePictureUrl != null) {
                        addUserToDatabase(name, email, uid , fcmToken , profilePictureUrl)
                    }else{
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

                    }

                    // Start Home activity
                    val intent = Intent(this, AllUsers::class.java)
                    startActivity(intent)
                    finish()
                    // Log and toast

                })

            } else {
                Toast.makeText(this, authTask.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
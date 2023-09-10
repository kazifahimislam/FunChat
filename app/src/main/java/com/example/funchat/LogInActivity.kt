package com.example.funchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LogInActivity : AppCompatActivity() {

    private lateinit var logIn : Button
    private lateinit var createANewAccount : TextView
    private lateinit var email: EditText
    private lateinit var password: EditText

//    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mDbRef: DatabaseReference

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        logIn = findViewById(R.id.logIn)
        createANewAccount = findViewById(R.id.newAccount)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        mAuth = FirebaseAuth.getInstance()

        createANewAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        logIn.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, AllUsers::class.java)
                        finish()
                        startActivity(intent)

                    } else {
                        val text = "Acoount Does Not Exist"
                        val duration = Toast.LENGTH_SHORT

                        val toast = Toast.makeText(this, text, duration)
                        toast.show()
                    }
                }

        }




    }
    private fun addUserToDatabase(name: String, email: String, uid: String, fcmToken: String , profilePicturUrl: String , timestamp: MutableMap<String, String>){

        mDbRef = FirebaseDatabase.getInstance().reference
        mDbRef.child("user").child(uid).setValue(User(name,email, uid, fcmToken ,profilePicturUrl , timestamp ))
    }

//    private fun signInGoogle() {
//        val signInIntent = googleSignInClient.signInIntent
//        launcher.launch(signInIntent)
//    }
//
//    private val launcher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//                handleResults(task)
//            }
//        }
//
//    private fun handleResults(task: Task<GoogleSignInAccount>) {
//        if (task.isSuccessful) {
//            val account: GoogleSignInAccount? = task.result
//            if (account != null) {
//                updateUI(account)
//            }
//
//        } else
//            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
//    }
//
//
//
//    private fun updateUI(account: GoogleSignInAccount) {
//        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//        mAuth.signInWithCredential(credential).addOnCompleteListener { authTask ->
//            if (authTask.isSuccessful) {
//                val currentUser = mAuth.currentUser
//                val uid = currentUser?.uid
//                val name = currentUser?.displayName
//                val email = currentUser?.email
//
//                // Add user to the database
//                if (uid != null && name != null && email != null) {
//                    addUserToDatabase(name, email, uid)
//                }
//
//                // Start Home activity
//                val intent = Intent(this, Home::class.java)
//                startActivity(intent)
//                finish()
//            } else {
//                Toast.makeText(this, authTask.exception.toString(), Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//    }
}

package com.example.funchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class AllUsers : AppCompatActivity() {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_users)



        FirebaseApp.initializeApp(this)

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {

            onBackButtonClicked()

        }


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM Token", token)
                // Save or use the token for sending notifications
            } else {
                Log.e("FCM Token", "Token retrieval failed", task.exception)
            }
        }



     mAuth = FirebaseAuth.getInstance()

        mDbRef= FirebaseDatabase.getInstance().getReference()
        mDbRef.keepSynced(true)


     userList = ArrayList()
        adapter = UserAdapter(this, userList)


        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        val logoutImageButton: ImageButton = findViewById(R.id.logoutImageButton)
        logoutImageButton.setOnClickListener {
            showPopupMenu(logoutImageButton)
        }

        mDbRef.child("users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {


                userList.clear()
                for (postSnapshot in snapshot.children){

                    val currentUser = postSnapshot.getValue(User::class.java)

                    if (mAuth.currentUser?.uid != currentUser?.uid){
                        userList.add(currentUser!!)
                    }


                }
                adapter.notifyDataSetChanged()

                }


            override fun onCancelled(error: DatabaseError) {

            }
        })
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        if (account != null) {
//            // Retrieve Google contacts
//            retrieveGoogleContacts(account)
//        }


    }
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.logout -> {
                    mAuth.signOut()
                    val intent = Intent(this, GoogleSignUp::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    fun onBackButtonClicked() {
        finish()
    }






}


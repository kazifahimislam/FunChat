package com.example.funchat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var recentChatsRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<RecentChats>
    private lateinit var adapter: RecentChatsAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        mDbRef= FirebaseDatabase.getInstance().getReference()
        mDbRef.keepSynced(true)


        val allUsers = findViewById<ImageButton>(R.id.allUsers)


        allUsers.setOnClickListener {
            val intent = Intent(this, AllUsers::class.java)
            startActivity(intent)
        }

        mAuth = FirebaseAuth.getInstance()

        userList = ArrayList()


        adapter = RecentChatsAdapter(this, userList)

        recentChatsRecyclerView = findViewById(R.id.recentChatsRecyclerView)
        recentChatsRecyclerView.layoutManager = LinearLayoutManager(this)
        recentChatsRecyclerView.adapter = adapter


        val logoutImageButton: ImageButton = findViewById(R.id.logoutImageButton)
        logoutImageButton.setOnClickListener {
            showPopupMenu(logoutImageButton)
        }


        val recentChatsUid = mAuth.currentUser?.uid



        if (recentChatsUid != null) {
            mDbRef.child("users").child(recentChatsUid).child("recentChats").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (postSnapshot in snapshot.children) {
                        val userId = postSnapshot.key
                        // Now, you have the UID of users in recentChats
                        // You can fetch the user details from the "users" node using these UIDs
                        if (userId != null) {
                            mDbRef.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                    val currentUser = userSnapshot.getValue(RecentChats::class.java)
                                    if (currentUser != null && mAuth.currentUser?.uid != currentUser.uid) {
                                        userList.add(currentUser)
                                    }
                                    adapter.notifyDataSetChanged()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle errors if necessary
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors if necessary
                }
            })
        }

    }
    override fun onPause() {
        super.onPause()
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("lastActivity", "Home")
        editor.apply()
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
    }



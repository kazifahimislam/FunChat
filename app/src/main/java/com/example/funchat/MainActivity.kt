package com.example.funchat

import RecentChatsAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var recentChatsRecyclerView: RecyclerView
    private lateinit var recentChatsAdapter: RecentChatsAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        recentChatsRecyclerView = findViewById(R.id.recentChatsRecyclerView)
//        recentChatsAdapter = RecentChatsAdapter(this, emptyList()) { chatUid ->
//            val intent = Intent(this, ChatActivity::class.java)
//            intent.putExtra("chatUid", chatUid)
//            startActivity(intent)
//        }

        // Set up the RecyclerView with your recent chats adapter
        recentChatsRecyclerView.layoutManager = LinearLayoutManager(this)
        recentChatsRecyclerView.adapter = recentChatsAdapter

        // Initialize the Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().reference

        // Fetch and populate recent chats data from your database
        fetchRecentChatsFromDatabase()
    }

    private fun fetchRecentChatsFromDatabase() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserUid != null) {
            // Reference to the "recentChats" node for the current user
            val recentChatsReference = databaseReference.child("users").child(currentUserUid)
                .child("recentChats")

            // Listen for changes in the "recentChats" node
            recentChatsReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val recentChatUids = mutableListOf<String>()

                    // Iterate through the dataSnapshot to get recent chat UIDs
                    for (childSnapshot in dataSnapshot.children) {
                        if (childSnapshot.value as? Boolean == true) {
                            recentChatUids.add(childSnapshot.key ?: "")
                        }
                    }

                    // Update the adapter with the recent chat UIDs
                    recentChatsAdapter.updateData(recentChatUids)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors if necessary
                }
            })
        }
    }
}
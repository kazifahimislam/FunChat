package com.example.funchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: Button
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef : DatabaseReference
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private var isKeyboardOpen = false

    var receiverRoom : String? = null
    var senderRoom : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val displayUserName = intent.getStringExtra("name")

        val auth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser
        val currentUsername = currentUser?.displayName.toString()



        val profilePictureUrl = intent.getStringExtra("profilePictureUrl")
        val profileImageView = findViewById<ImageView>(R.id.userPic)

        Glide.with(this)
            .load(profilePictureUrl)
            .placeholder(R.drawable.img_4) // Placeholder image while loading
            .error(R.drawable.meerkat) // Error image if loading fails
            .into(profileImageView)
        findViewById<TextView>(R.id.txtProfile).text =  displayUserName

        FirebaseApp.initializeApp(this)

        imageView = findViewById(R.id.userPic)
        textView = findViewById(R.id.txtProfile)

        imageView.setOnClickListener{



            val intent = Intent(this, OtherUserPic::class.java)


            intent.putExtra("name", displayUserName)
            intent.putExtra("profilePictureUrl", profilePictureUrl)

            startActivity(intent)
        }
        textView.setOnClickListener{
            val intent = Intent(this, UserProfile::class.java)
            intent.putExtra("name", displayUserName)
            intent.putExtra("profilePictureUrl", profilePictureUrl)

            startActivity(intent)
        }

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val fcmToken = intent.getStringExtra("fcmToken")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

//        chatRecyclerView.smoothScrollToPosition(0)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        val layoutManager = chatRecyclerView.layoutManager as LinearLayoutManager



        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val initialSize = messageList.size
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()


                        val lastItemPosition = messageList.size - 1
                        layoutManager.scrollToPositionWithOffset(lastItemPosition, 0)

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


        sendButton.setOnClickListener {
            val message = messageBox.text.toString().trim() // Trim any leading or trailing spaces

            if (message.isNotEmpty()) {
                val senderUid = FirebaseAuth.getInstance().currentUser!!.uid
                val receiverUid = intent.getStringExtra("uid")

                val messageObject = Message(message, senderUid)

                // Reference to the Firebase Realtime Database
                val databaseReference = FirebaseDatabase.getInstance().reference

                // Push the message to the "messages" node
                val senderMessageRef = databaseReference.child("chats").child(senderRoom!!).child("messages").push()
                val receiverMessageRef = databaseReference.child("chats").child(receiverRoom!!).child("messages").push()

                // Set the message content
                senderMessageRef.setValue(messageObject).addOnSuccessListener {
                    receiverMessageRef.setValue(messageObject).addOnSuccessListener {
                        // If the message is sent successfully, add the chat to recentChats
                        if (receiverUid != null) {
                            databaseReference.child("user").child(senderUid).child("recentChats").child(receiverUid).setValue(true)
                        }
                        if (receiverUid != null) {
                            databaseReference.child("user").child(receiverUid).child("recentChats").child(senderUid).setValue(true)
                        }


                    }
                }
                sendNotificationToReceiver("$fcmToken","$currentUsername", "$message" )

                messageBox.setText("")
            } else {
                // Handle the case where the message is empty, e.g., show an error message to the user
                // You can display a toast or a Snackbar to inform the user that the message is empty.
            }
        }
    }
    fun sendNotificationToReceiver(userToken: String, title: String, body: String) {
        // Replace with your Firebase Cloud Messaging Server Key
        val serverKey = "AAAAus_OMzs:APA91bFnC2TftpSTiZggKnKojj2YGKlSWkMMNer1MpBC_CLEitKQcPqpjcEPls-nnRo0TvAdsZnHInN2hyS7lJ0pletKgZa6QfIUg52v0duhnxZJwIO3kc4diOEJwlJyE60Tes-fWWe5"

        // Define the content type
        val contentType = "application/json; charset=utf-8".toMediaType()

        // Create a JSON payload for the notification
        val json = """
    {
        "to": "$userToken",
        "notification": {
            "title": "$title",
            "body": "$body"
        }
    }
    """.trimIndent()

        // Create an HTTP request
        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .post(json.toRequestBody(contentType))
            .addHeader("Authorization", "key=$serverKey")
            .build()

        // Create an OkHttpClient to execute the request
        val client = OkHttpClient()

        // Enqueue the request for asynchronous execution
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle the failure to send the notification
                // You can log or display an error message
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle the response from the FCM server
                // This block is called when the response is received
                // You can add further handling if needed
            }
        })
    }
}

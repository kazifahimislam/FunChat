package com.example.funchat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.FirebaseStorage
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
    private lateinit var sendImage: ImageView
    private lateinit var imageView: ImageView

    private lateinit var textView: TextView
    private var isKeyboardOpen = false

    var receiverRoom : String? = null
    var senderRoom : String? = null
    private val storageReference = FirebaseStorage.getInstance().reference
    val imagesRef = storageReference.child("images")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var selectedImageUri: Uri? = null

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
        sendImage = findViewById(R.id.sendImage)
        sendImage.setOnClickListener {
            // PICK INTENT picks item from data
            // and returned selected item
            val galleryIntent = Intent(Intent.ACTION_PICK)
            // here item is type of image
            galleryIntent.type = "image/*"
            // ActivityResultLauncher callback
            imagePickerActivityResult.launch(galleryIntent)
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

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        val layoutManager = chatRecyclerView.layoutManager as LinearLayoutManager

        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
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
            val message = messageBox.text.toString().trim()

            if (message.isNotEmpty()) {
                val senderUid = FirebaseAuth.getInstance().currentUser!!.uid
                val receiverUid = intent.getStringExtra("uid")

                val messageObject = Message(message, senderUid, false)

                val databaseReference = FirebaseDatabase.getInstance().reference

                val senderMessageRef = databaseReference.child("chats").child(senderRoom!!).child("messages").push()
                val receiverMessageRef = databaseReference.child("chats").child(receiverRoom!!).child("messages").push()

                senderMessageRef.setValue(messageObject).addOnSuccessListener {
                    receiverMessageRef.setValue(messageObject).addOnSuccessListener {
                        if (receiverUid != null) {
                            databaseReference.child("users").child(senderUid).child("recentChats").child(receiverUid).setValue(true)
                        }
                        if (receiverUid != null) {
                            databaseReference.child("users").child(receiverUid).child("recentChats").child(senderUid).setValue(true)
                        }
                    }
                }
                sendNotificationToReceiver("$fcmToken", currentUsername, message,"$profilePictureUrl")

                messageBox.setText("")
            } else {
                // Handle the case where the message is empty, e.g., show an error message to the user
            }
        }
    }

    private fun sendNotificationToReceiver(userToken: String, title: String, body: String, profilePictureUrl:String) {
        val serverKey = "AAAAus_OMzs:APA91bFnC2TftpSTiZggKnKojj2YGKlSWkMMNer1MpBC_CLEitKQcPqpjcEPls-nnRo0TvAdsZnHInN2hyS7lJ0pletKgZa6QfIUg52v0duhnxZJwIO3kc4diOEJwlJyE60Tes-fWWe5"
        val contentType = "application/json; charset=utf-8".toMediaType()
        val json = """
    {
        "to": "$userToken",
        "notification": {
            "title": "$title",
            "body": "$body",
            "profilePictureUrl": "$profilePictureUrl"
        }
    }
    """.trimIndent()

        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .post(json.toRequestBody(contentType))
            .addHeader("Authorization", "key=$serverKey")
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
            }
        })
    }

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                val imageUri: Uri? = result.data?.data
                val sd = getFileName(applicationContext, imageUri!!)
                val uploadTask = imagesRef.child("file/$sd").putFile(imageUri)

                uploadTask.addOnSuccessListener {
                    imagesRef.child("file/$sd").downloadUrl.addOnSuccessListener {
                        sendMessageWithImage(it.toString())
                    }.addOnFailureListener {
                        Log.e("Firebase", "Failed in downloading")
                    }
                }.addOnFailureListener {
                    Log.e("Firebase", "Image Upload fail")
                }
            }
        }

    private fun sendMessageWithImage(imageUrl: String) {
        val senderUid = FirebaseAuth.getInstance().currentUser!!.uid
        val receiverUid = intent.getStringExtra("uid")

        val messageObject = Message(imageUrl, senderUid, isImage = true)

        val databaseReference = FirebaseDatabase.getInstance().reference

        val senderMessageRef = databaseReference.child("chats").child(senderRoom!!).child("images").push()
        val receiverMessageRef = databaseReference.child("chats").child(receiverRoom!!).child("images").push()

        senderMessageRef.setValue(messageObject).addOnSuccessListener {
            receiverMessageRef.setValue(messageObject).addOnSuccessListener {
                if (receiverUid != null) {
                    databaseReference.child("users").child(senderUid).child("recentChats").child(receiverUid).setValue(true)
                }
                if (receiverUid != null) {
                    databaseReference.child("users").child(receiverUid).child("recentChats").child(senderUid).setValue(true)
                }
            }
        }
    }


    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }
}

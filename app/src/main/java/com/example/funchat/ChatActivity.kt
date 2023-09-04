package com.example.funchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import android.graphics.Rect
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.bumptech.glide.Glide

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

        val displayName = intent.getStringExtra("name")

        val profilePictureUrl = intent.getStringExtra("profilePictureUrl")
        val profileImageView = findViewById<ImageView>(R.id.userPic)

        Glide.with(this)
            .load(profilePictureUrl)
            .placeholder(R.drawable.img_4) // Placeholder image while loading
            .error(R.drawable.meerkat) // Error image if loading fails
            .into(profileImageView)
        findViewById<TextView>(R.id.txtProfile).text =  displayName

        FirebaseApp.initializeApp(this)

        imageView = findViewById(R.id.userPic)
        textView = findViewById(R.id.txtProfile)

        imageView.setOnClickListener{



            val intent = Intent(this, OtherUserPic::class.java)


            intent.putExtra("name", displayName)
            intent.putExtra("profilePictureUrl", profilePictureUrl)

            startActivity(intent)
        }
        textView.setOnClickListener{
            val intent = Intent(this, UserProfile::class.java)
            intent.putExtra("name", displayName)
            intent.putExtra("profilePictureUrl", profilePictureUrl)

            startActivity(intent)
        }

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
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

        // Add a listener for changes in the keyboard's visibility
        messageBox.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = Rect()
                messageBox.getWindowVisibleDisplayFrame(rect)
                val screenHeight = messageBox.height
                val keypadHeight = screenHeight - rect.bottom
                val isKeyboardOpen = keypadHeight > screenHeight * 0.15

                if (isKeyboardOpen) {
                    // The keyboard is open, handle this scenario if needed.
                    // For example, you can refresh the chat list and scroll to the recent item.
                } else {
                    // The keyboard is closed, handle this scenario if needed.
                    // For example, you can refresh the chat list and scroll to the recent item.
                }
            }
        })

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

                    if (messageList.isNotEmpty()) {
                        val lastItemPosition = messageList.size - 1
                        layoutManager.scrollToPositionWithOffset(lastItemPosition, 0)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        sendButton.setOnClickListener {
            val message = messageBox.text.toString()
            val messageObject = Message(message, senderUid)

            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                    if (isKeyboardOpen) {
                        chatRecyclerView.postDelayed({
                            chatRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                        }, 100)
                    }
                }
            messageBox.setText("")
        }
    }
}

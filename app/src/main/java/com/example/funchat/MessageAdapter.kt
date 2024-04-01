package com.example.funchat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_RECEIVE = 1
    private val ITEM_SENT = 2
    private val ITEM_IMAGE_RECEIVE = 3
    private val ITEM_IMAGE_SEND = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ITEM_SENT -> {
                val view: View = inflater.inflate(R.layout.sent, parent, false)
                SentViewHolder(view)
            }
            ITEM_RECEIVE -> {
                val view: View = inflater.inflate(R.layout.receive, parent, false)
                ReceiveViewHolder(view)
            }
            ITEM_IMAGE_RECEIVE -> {
                val view: View = inflater.inflate(R.layout.image_message_item, parent, false)
                ImageMessageViewHolder(view)
            }
            ITEM_IMAGE_SEND -> {
                val view: View = inflater.inflate(R.layout.image_sent, parent, false)
                ImageSentViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        when (holder.itemViewType) {
            ITEM_SENT -> {
                val sentViewHolder = holder as SentViewHolder
                sentViewHolder.sentMessage.text = currentMessage.message
                sentViewHolder.sentTime.text = currentMessage.timestamp
            }
            ITEM_RECEIVE -> {
                val receiveViewHolder = holder as ReceiveViewHolder
                receiveViewHolder.receiveMessage.text = currentMessage.message
            }
            ITEM_IMAGE_RECEIVE -> {
                val imageViewHolder = holder as ImageMessageViewHolder
                imageViewHolder.bind(currentMessage)


            }
            ITEM_IMAGE_SEND -> {
                val imageViewHolder = holder as ImageSentViewHolder
                imageViewHolder.bind(currentMessage)
            }

        }


    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        return if (currentMessage.isImage) {
            if (FirebaseAuth.getInstance().currentUser?.uid == currentMessage.senderId) {
                ITEM_IMAGE_SEND
            }else {
                ITEM_IMAGE_RECEIVE
            }

        }

        else if (FirebaseAuth.getInstance().currentUser?.uid == currentMessage.senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    inner class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage: TextView = itemView.findViewById(R.id.txt_sent_message)
        val sentTime : TextView = itemView.findViewById(R.id.sentMessageTime)
    }

    inner class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView = itemView.findViewById(R.id.txt_receive_message)
    }

    inner class ImageMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageMessageView)

        fun bind(message: Message) {
            // Load image using Glide library
            Glide.with(context)
                .load(message.message) // Assuming message.message contains the image URL
                .into(imageView)


            imageView.setOnClickListener {
                // Handle click event here, for example, open a new activity to show the image in full screen
                val intent = Intent(context, FullScreenImageActivity::class.java)
                intent.putExtra("imageUrl", message.message)
                context.startActivity(intent)
            }
        }
    }
    inner class ImageSentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageReceiveView)

        fun bind(message: Message) {
            // Load image using Glide library
            Glide.with(context)
                .load(message.message) // Assuming message.message contains the image URL
                .into(imageView)

            imageView.setOnClickListener {
                // Handle click event here, for example, open a new activity to show the image in full screen
                val intent = Intent(context, FullScreenImageActivity::class.java)
                intent.putExtra("imageUrl", message.message)
                context.startActivity(intent)
            }
        }

    }
}

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.funchat.R
import com.example.funchat.User
import com.google.firebase.database.*

class RecentChatsAdapter(
    private val context: Context,
    private val currentUserUid: String, // Current user's UID as a String
    private val onItemClick: (String) -> Unit // Callback for item click
) : RecyclerView.Adapter<RecentChatsAdapter.RecentChatViewHolder>() {
    // ...

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    private var recentChatUids: List<String> = emptyList()

    init {
        // Listen for changes in the "recentChats" node for the current user
        databaseReference.child("recentChats").child(currentUserUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val chatUids = dataSnapshot.children.map { it.key.toString() }
                        updateData(chatUids)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recent_chat_item, parent, false)
        return RecentChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentChatViewHolder, position: Int) {
        val chatUid = recentChatUids[position]

        // Set an item click listener
        holder.itemView.setOnClickListener {
            onItemClick(chatUid)
        }

        // Fetch and display relevant data for the chat here.
        // For example, fetch the chat user's name from the database and set it to a TextView.
        // You can also fetch other relevant data like the last message, profile picture, etc.

        val chatNameTextView: TextView = holder.itemView.findViewById(R.id.recentUserName)

        // Fetch and display the chat user's name from the database
        databaseReference.child("users").child(chatUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val chatUser = dataSnapshot.getValue(User::class.java)
                    chatUser?.let {
                        chatNameTextView.text = it.name
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }

    override fun getItemCount(): Int {
        return recentChatUids.size
    }

    // Update the adapter's data when needed (e.g., when you fetch new recent chats)
    internal fun updateData(newData: List<String>) {
        recentChatUids = newData
        notifyDataSetChanged()
    }

    inner class RecentChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

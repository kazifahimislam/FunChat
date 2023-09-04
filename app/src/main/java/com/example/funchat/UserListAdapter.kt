import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.funchat.ChatActivity
import com.example.funchat.R
import com.example.funchat.User

class UserListAdapter(val context: Context, val userList: List<User>): RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.textName.text = currentUser.name

        Glide.with(context)
            .load(currentUser.profilePictureUrl) // Load the profile picture URL
            .placeholder(R.drawable.img_4) // Placeholder image
            .error(R.drawable.meerkat) // Error image (optional)
            .into(holder.profileImageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", currentUser.name)
            intent.putExtra("uid", currentUser.uid)
            intent.putExtra("profilePictureUrl", currentUser.profilePictureUrl)
            context.startActivity(intent)
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.text_name)
        val profileImageView = itemView.findViewById<ImageView>(R.id.userProfilePic)
    }
}

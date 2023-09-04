import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.funchat.R
import com.example.funchat.User

class UserListActivity : AppCompatActivity() {

    private lateinit var userDatabaseHelper: UserDatabaseHelper
    private lateinit var userListAdapter: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        userDatabaseHelper = UserDatabaseHelper(this)

        // Load user data from your SQLite database
        val userList = loadUsersFromDatabase()

        // Initialize your RecyclerView and set the adapter
        val recyclerView = findViewById<RecyclerView>(R.id.offlineUsers)
        userListAdapter = UserListAdapter(this, userList)
        recyclerView.adapter = userListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadUsersFromDatabase(): List<User> {
        val db = userDatabaseHelper.readableDatabase
        val userList = mutableListOf<User>()

        val cursor = db.query("users", null, null, null, null, null, null)

//        while (cursor.moveToNext()) {
//            val id = cursor.getLong(cursor.getColumnIndex("_id"))
//            val name = cursor.getString(cursor.getColumnIndex("name"))
//            val email = cursor.getString(cursor.getColumnIndex("email"))
//
//            val user = User(id, name, email)
//            userList.add(user)
//        }

        cursor.close()
        db.close()

        return userList
    }
}

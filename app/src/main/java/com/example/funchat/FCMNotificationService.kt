import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FCMNotificationService : FirebaseMessagingService() {

    private val TAG = "FCMNotificationService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Store the FCM token in Firebase Realtime Database
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            storeTokenInDatabase(uid, token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle incoming FCM messages here
        Log.d(TAG, "Message data payload: ${remoteMessage.data}")
    }

    private fun storeTokenInDatabase(uid: String, token: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val tokenRef = databaseReference.child("user").child(uid).child("fcmToken")
        tokenRef.setValue(token)
    }
}

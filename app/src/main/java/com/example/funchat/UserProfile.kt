package com.example.funchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class UserProfile : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val displayName = intent.getStringExtra("name")
        val profilePictureUrl = intent.getStringExtra("profilePictureUrl") // Get the profile picture URL

        findViewById<TextView>(R.id.userName).text = displayName
        val profileImageView = findViewById<ImageView>(R.id.userPic)

        Glide.with(this)
            .load(profilePictureUrl)
            .placeholder(R.drawable.img_4) // Placeholder image while loading
            .error(R.drawable.meerkat) // Error image if loading fails
            .into(profileImageView)

        profileImageView.setOnClickListener{
            val intent = Intent(this, OtherUserPic::class.java)
            intent.putExtra("profilePictureUrl", profilePictureUrl)
            startActivity(intent)
        }


    }
}
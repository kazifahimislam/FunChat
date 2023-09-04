package com.example.funchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide

class OtherUserPic : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user_pic)
        val profilePictureUrl = intent.getStringExtra("profilePictureUrl")
        val profileImageView = findViewById<ImageView>(R.id.otherUserPic)

        Glide.with(this)
            .load(profilePictureUrl)
            .placeholder(R.drawable.img_4) // Placeholder image while loading
            .error(R.drawable.meerkat) // Error image if loading fails
            .into(profileImageView)
    }
}
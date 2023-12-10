package com.example.funchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide

class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)
        val profilePictureUrl = intent.getStringExtra("imageUrl")
        val profileImageView = findViewById<ImageView>(R.id.fullscreenPic)

        Glide.with(this)
            .load(profilePictureUrl)
            .into(profileImageView)
    }
}
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
            .placeholder(R.drawable.img_4) // Placeholder image while loading
            .error(R.drawable.meerkat) // Error image if loading fails
            .into(profileImageView)
    }
}
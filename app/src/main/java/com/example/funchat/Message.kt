package com.example.funchat

import android.content.IntentSender

class Message {
    var isImage: Boolean = false
    var message: String? = null
    var senderId: String? = null

    constructor(){}

    constructor(message: String, senderId: String?, isImage: Boolean){
        this.isImage = isImage
        this.message = message
        this.senderId = senderId

    }
}
package com.example.funchat

import android.content.IntentSender

class Message {
    var isImage: Boolean = false
    var message: String? = null
    var senderId: String? = null
    var timestamp: String? = null

    constructor(){}

    constructor(message: String, senderId: String?, isImage: Boolean,timestamp: String?){
        this.isImage = isImage
        this.message = message
        this.senderId = senderId
        this.timestamp = timestamp

    }
}
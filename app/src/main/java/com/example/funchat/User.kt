package com.example.funchat

import com.google.firebase.messaging.FirebaseMessaging

class User {
    var name : String? = null
    var email : String? = null
    var uid : String? = null
    var fcmToken : String? = null
    var profilePictureUrl : String? = null

    constructor(){}

    constructor(name: String?, email: String?, uid: String? , fcmToken  : String?, profilePictureUrl : String?
    ){
        this.name = name
        this.email = email
        this.uid = uid
        this.fcmToken = fcmToken
        this.profilePictureUrl = profilePictureUrl

    }
    fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result

            }
        }
    }


}
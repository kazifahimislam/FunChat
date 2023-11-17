package com.example.funchat

class User {
    var name : String? = null
    var email : String? = null
    var uid : String? = null
    var fcmToken : String? = null
    var profilePictureUrl : String? = null
    var timestamp : String? = null



    constructor(){}

    constructor(name: String?, email: String?, uid: String?, fcmToken: String?, profilePictureUrl: String?, timestamp: MutableMap<String, String>){
        this.name = name
        this.email = email
        this.uid = uid
        this.fcmToken = fcmToken
        this.profilePictureUrl = profilePictureUrl
        this.timestamp = timestamp.toString()
//        this.lastMessage = lastMessage

    }



}
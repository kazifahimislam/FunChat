package com.example.funchat

class RecentChats {

    var uid : String? = null
    var profilePictureUrl : String? = null
    var name : String? = null

    constructor(){}

    constructor(uid: String?,profilePictureUrl: String?,name: String?){
        this.uid = uid
        this.profilePictureUrl = profilePictureUrl
        this.name = name
    }
}
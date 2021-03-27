package com.foodkapev.foodsharingrus.Model

class User {

    private var username: String = ""
    private var fullname: String = ""
    private var bio: String = ""
    private var image: String = ""
    private var uid: String = ""
    private var email: String = ""

    constructor()

    constructor(username: String, fullname: String, bio: String, image: String, uid: String, email: String) {
        this.username = username
        this.fullname = fullname
        this.bio = bio
        this.image = image
        this.uid = uid
        this.email = email
    }

    fun getUsername(): String {
        return username
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun getFullname(): String {
        return fullname
    }

    fun setFullname(fullname: String) {
        this.fullname = fullname
    }

    fun getBio(): String {
        return bio
    }

    fun setBio(bio: String) {
        this.bio = bio
    }

    fun getImage(): String {
        return image
    }

    fun setImage(image: String) {
        this.image = image
    }

    fun getUid(): String {
        return uid
    }

    fun setUid(uid: String) {
        this.uid = uid
    }

    fun getEmail(): String {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }
}
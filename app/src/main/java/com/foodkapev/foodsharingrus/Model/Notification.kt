package com.foodkapev.foodsharingrus.Model

class Notification {

    private var userId: String = ""
    private var text: String = ""
    private var postId: String = ""
    private var isPost: Boolean = false

    constructor()

    constructor(userId: String, text: String, postId: String, isPost: Boolean) {
        this.userId = userId
        this.text = text
        this.postId = postId
        this.isPost = isPost
    }

    fun getUserId() = userId
    fun getText() = text
    fun getPostId() = postId
    fun getIsPost() = isPost

    fun setUserId(userId: String) {
        this.userId = userId
    }

    fun setText(text: String) {
        this.text = text
    }

    fun setPostId(postId: String) {
        this.postId = postId
    }

    fun setIsPost(isPost: Boolean) {
        this.isPost = isPost
    }
}
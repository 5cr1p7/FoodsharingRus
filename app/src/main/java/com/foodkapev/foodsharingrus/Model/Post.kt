package com.foodkapev.foodsharingrus.Model

class Post {

    private var postId: String = ""
    private var postImage: String = ""
    private var publisher: String = ""
    private var description: String = ""
    private var title: String = ""
    private var time: String = ""
    private var type: String = ""
    private var location: String = ""

    constructor()

    constructor(postId: String, postImage: String, publisher: String, description: String,
                title: String, time: String, location: String) {
        this.postId = postId
        this.postImage = postImage
        this.publisher = publisher
        this.description = description
        this.title = title
        this.time = time
        this.location = location
    }

    fun getPostId(): String = postId

    fun getPostImage(): String = postImage

    fun getPublisher(): String = publisher

    fun getDescription(): String = description

    fun getTitle(): String = title

    fun getTime(): String = time

    fun getType(): String = type

    fun getLocation(): String = location

    fun setPostId(postId: String) {
        this.postId = postId
    }

    fun setPostImage(postImage: String) {
        this.postImage = postImage
    }

    fun setPublisher(publisher: String) {
        this.publisher = publisher
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setTime(time: String) {
        this.time = time
    }

    fun setType(type: String) {
        this.type = type
    }

    fun setLocation(location: String) {
        this.location = location
    }
}
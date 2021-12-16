package com.foodkapev.foodsharingrus.domain.models

data class Post(
    var postId: String = "",
    var postImage: String = "",
    var publisher: String = "",
    var description: String = "",
    var title: String = "",
    var time: String = "",
    var type: String = "",
    var location: String = ""
)
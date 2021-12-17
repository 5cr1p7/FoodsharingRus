package com.foodkapev.foodsharingrus.domain.models

class Notification(
    var userId: String = "",
    var text: String = "",
    var postId: String = "",
    var isPost: Boolean = true
)
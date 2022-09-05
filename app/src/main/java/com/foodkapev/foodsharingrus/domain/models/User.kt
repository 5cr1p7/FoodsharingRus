package com.foodkapev.foodsharingrus.domain.models

data class User(
    var username: String = "",
    var fullname: String = "",
    var bio: String = "",
    var image: String = "",
    var uid: String = "",
    var email: String = ""
)
package com.foodkapev.foodsharingrus.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class City(val city: String, val region: String)
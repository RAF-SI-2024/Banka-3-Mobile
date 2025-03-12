package com.example.banka_3_mobile.user.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginPostRequest(
    val email: String,
    val password: String
)


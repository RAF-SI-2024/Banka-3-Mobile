package com.example.banka_3_mobile.user.model

import kotlinx.serialization.Serializable
import java.util.Date


@Serializable
data class ClientGetResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val address: String,
    val phone: String,
    val gender: String,
    val birthDate: String,
    val jmbg: String,
    val username: String
)


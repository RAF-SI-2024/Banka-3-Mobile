package com.example.banka_3_mobile.verification.model

import kotlinx.serialization.Serializable

@Serializable
data class NewCardVerificationDetails(
    /*
    String name
    String  issuer
    String type
    String  accountNumber
     */
    val name: String,
    val issuer: String,
    val type: String,
    val accountNumber: String
)

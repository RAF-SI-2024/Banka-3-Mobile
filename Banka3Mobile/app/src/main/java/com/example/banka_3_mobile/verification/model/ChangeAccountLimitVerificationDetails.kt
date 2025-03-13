package com.example.banka_3_mobile.verification.model

import kotlinx.serialization.Serializable

@Serializable
data class ChangeAccountLimitVerificationDetails(
    val accountNumber: String,
    val oldLimit: Float,
    val newLimit: Float
)

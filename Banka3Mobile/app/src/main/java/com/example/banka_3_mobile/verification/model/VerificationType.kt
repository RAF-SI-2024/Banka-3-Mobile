package com.example.banka_3_mobile.verification.model

import kotlinx.serialization.Serializable

@Serializable
enum class VerificationType {
    LOGIN,
    LOAN,
    PAYMENT,
    TRANSFER,
    CHANGE_LIMIT
}
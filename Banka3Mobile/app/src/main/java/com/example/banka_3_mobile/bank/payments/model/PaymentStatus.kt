package com.example.banka_3_mobile.bank.payments.model

import kotlinx.serialization.Serializable

@Serializable
enum class PaymentStatus {
    COMPLETED,
    CANCELED,
    PENDING_CONFIRMATION
}

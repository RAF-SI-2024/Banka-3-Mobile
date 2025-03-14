package com.example.banka_3_mobile.bank.payments

import kotlinx.serialization.Serializable

@Serializable
enum class PaymentStatus {
    COMPLETED,
    CANCELED,
    PENDING_CONFIRMATION
}

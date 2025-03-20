package com.example.banka_3_mobile.bank.payments.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentGetResponse(
    val id: Long? = null,
    val senderName: String? = null,
    val amount: Float,
    val date: String,
    val status: PaymentStatus,
    val cardNumber: String? = null
)



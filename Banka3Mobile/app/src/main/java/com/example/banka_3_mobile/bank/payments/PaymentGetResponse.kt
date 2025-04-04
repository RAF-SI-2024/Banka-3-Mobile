package com.example.banka_3_mobile.bank.payments

import kotlinx.serialization.Serializable

@Serializable
data class PaymentGetResponse(
    val id: Long? = null,
    val senderName: String,
    val amount: String,
    val date: String,
    val status: PaymentStatus,
    val cardNumber: String
)



package com.example.banka_3_mobile.verification.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentAndTransferVerificationDetails(
    val fromAccountNumber: Long,
    val toAccountNumber: Long,
    val amount: Float
)


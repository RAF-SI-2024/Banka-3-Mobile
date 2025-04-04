package com.example.banka_3_mobile.bank.payments.new_payment.model

import kotlinx.serialization.Serializable

@Serializable
data class NewPaymentPostRequest(
    val senderAccountNumber: String,
    val receiverAccountNumber: String,
    val amount: Float,
    val paymentCode: String,
    val purposeOfPayment: String,
    val referenceNumber: String? = null,
)
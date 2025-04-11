package com.example.banka_3_mobile.bank.payments.details.model

import com.example.banka_3_mobile.bank.payments.model.PaymentStatus
import kotlinx.serialization.Serializable

@Serializable
data class PaymentDetailsGetResponse(
    val id: Long,
    val senderName: String? = null,
    val amount: Float,
    val accountNumberReceiver: String,
    val paymentCode: String? = null,
    val purposeOfPayment: String? = null,
    val referenceNumber: String? = null,
    val date: String,
    val status: PaymentStatus,
    val cardNumber: String? = null
)

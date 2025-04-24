package com.example.banka_3_mobile.bank.payments.ips.model

data class IPSModel(
    val accountNumber: String? = null,
    val recipientName: String? = null,
    val amount: String? = null,
    val payer: String? = null,
    val paymentCode: String? = null,
    val purpose: String? = null,
    val referenceNumber: String? = null,
)

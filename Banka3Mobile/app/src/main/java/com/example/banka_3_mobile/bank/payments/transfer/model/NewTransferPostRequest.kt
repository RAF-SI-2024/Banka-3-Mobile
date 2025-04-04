package com.example.banka_3_mobile.bank.payments.transfer.model

import kotlinx.serialization.Serializable

@Serializable
data class NewTransferPostRequest(
    val senderAccountNumber: String,
    val receiverAccountNumber: String,
    val amount: Float,
)

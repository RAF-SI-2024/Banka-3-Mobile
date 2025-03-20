package com.example.banka_3_mobile.bank.payments.payee.model

import kotlinx.serialization.Serializable

@Serializable
data class PayeeGetResponse(
    val id: Long? = null,
    val name: String,
    val accountNumber: String
)

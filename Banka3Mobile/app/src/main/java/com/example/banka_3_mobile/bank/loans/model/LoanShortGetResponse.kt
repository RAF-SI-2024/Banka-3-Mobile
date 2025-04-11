package com.example.banka_3_mobile.bank.loans.model

import kotlinx.serialization.Serializable

@Serializable
data class LoanShortGetResponse(
    val id: Long,
    val loanNumber: String,
    val type: LoanType,
    val amount: Float
)

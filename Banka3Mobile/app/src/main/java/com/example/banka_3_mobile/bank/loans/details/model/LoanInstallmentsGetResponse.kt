package com.example.banka_3_mobile.bank.loans.details.model

import kotlinx.serialization.Serializable

@Serializable
data class LoanInstallmentsGetResponse(
    val amount: Float,
    val interestRate: Float,
    val expectedDueDate: String,
    val actualDueDate: String? = null,
    val installmentStatus: InstallmentStatus
)

@Serializable
enum class InstallmentStatus {
    PAID, UNPAID, LATE
}

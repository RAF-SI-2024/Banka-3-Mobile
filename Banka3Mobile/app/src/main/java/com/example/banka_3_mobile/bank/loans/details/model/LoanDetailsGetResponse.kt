package com.example.banka_3_mobile.bank.loans.details.model

import com.example.banka_3_mobile.bank.loans.model.LoanType
import kotlinx.serialization.Serializable

@Serializable
data class LoanDetailsGetResponse(
    val id: Long,
    val loanNumber: String,
    val type: LoanType,
    val amount: Float,
    val repaymentPeriod: Int,
    val nominalInterestRate: Float,
    val effectiveInterestRate: Float,
    val startDate: String,
    val dueDate: String,
    val nextInstallmentAmount: Float,
    val nextInstallmentDate: String,
    val remainingDebt: Float,
    val currencyCode: String,
    val status: LoanStatus
)

@Serializable
enum class LoanStatus {
    PENDING, APPROVED, REJECTED
}

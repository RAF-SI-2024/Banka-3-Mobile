package com.example.banka_3_mobile.bank.loans.model

import kotlinx.serialization.Serializable

@Serializable
data class LoanRequestGetResponse(
    val type: LoanType,
    val amount: Float,
    val purpose: String,
    val monthlyIncome: Float,
    val employmentStatus: EmploymentStatus,
    val employmentDuration: Int? = null,
    val repaymentPeriod: Int? = null,
    val contactPhone: String,
    val accountNumber: String,
    val currencyCode: String,
    val interestRateType: InterestRateType,
    val createdAt: String,
    val status: LoanRequestStatus
)


@Serializable
enum class EmploymentStatus {
    PERMANENT, TEMPORARY, UNEMPLOYED
}

@Serializable
enum class LoanRequestStatus {
    PENDING, APPROVED, REJECTED
}

@Serializable
enum class LoanType {
    CASH, MORTGAGE, AUTO, REFINANCING, STUDENT
}

@Serializable
enum class InterestRateType {
    FIXED, VARIABLE
}
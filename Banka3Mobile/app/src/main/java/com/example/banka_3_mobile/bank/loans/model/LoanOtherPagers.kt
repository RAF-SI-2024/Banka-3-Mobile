package com.example.banka_3_mobile.bank.loans.model

import com.example.banka_3_mobile.bank.loans.details.model.LoanDetailsGetResponse
import com.example.banka_3_mobile.bank.loans.details.model.LoanInstallmentsGetResponse
import kotlinx.serialization.Serializable

@Serializable
data class LoanShortPageResponse(
    val content: List<LoanShortGetResponse>

)

@Serializable
data class InstallmentsPageResponse(
    val content: List<LoanInstallmentsGetResponse>
)

@Serializable
data class LoanDetailsPageResponse(
    val content: List<LoanDetailsGetResponse>

)
package com.example.banka_3_mobile.bank.loans.details

import com.example.banka_3_mobile.bank.loans.details.model.LoanDetailsGetResponse
import com.example.banka_3_mobile.bank.loans.details.model.LoanInstallmentsGetResponse

interface LoanDetailsContract {
    data class LoanDetailsUiState(
        val loan: LoanDetailsGetResponse? = null,
        val loanInstallments: List<LoanInstallmentsGetResponse> = emptyList(),
        val fetching: Boolean = true,
        val error: String? = null,
    )
}
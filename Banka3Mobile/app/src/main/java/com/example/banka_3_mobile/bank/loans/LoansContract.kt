package com.example.banka_3_mobile.bank.loans

import com.example.banka_3_mobile.bank.loans.model.LoanRequestGetResponse
import com.example.banka_3_mobile.bank.loans.model.LoanShortGetResponse

interface LoansContract {
    data class LoansUiState(
        val loans: List<LoanShortGetResponse> = emptyList(),
        val loanRequests: List<LoanRequestGetResponse> = emptyList(),
        val fetching: Boolean = true,
        val error: String? = null,
    )
}
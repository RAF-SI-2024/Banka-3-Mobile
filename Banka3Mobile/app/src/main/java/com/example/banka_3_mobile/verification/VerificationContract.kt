package com.example.banka_3_mobile.verification

import com.example.banka_3_mobile.bank.payments.PaymentGetResponse

interface VerificationContract {
    data class VerificationUiState(
        val allPaymentsRequests: List<PaymentGetResponse> = emptyList(),
        val fetching: Boolean = false,
        val error: String? = null,
    )

    sealed class VerificationUIEvent {
        data object VerifyPending: VerificationUIEvent()
        data object DeclinePending: VerificationUIEvent()
    }
}
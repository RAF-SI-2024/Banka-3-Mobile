package com.example.banka_3_mobile.verification

import com.example.banka_3_mobile.verification.model.VerificationRequest

interface VerificationContract {
    data class VerificationUiState(
        val activeRequests: List<VerificationRequest> = emptyList(),
        val requestHistory: List<VerificationRequest> = emptyList(),
        val fetching: Boolean = false,
        val error: String? = null,
        val isRefreshing: Boolean = false,
    )

    sealed class VerificationUIEvent {
        data class VerifyPending(val id: Long): VerificationUIEvent()
        data class DeclinePending(val id: Long): VerificationUIEvent()
        data object PullToRefreshTrigger: VerificationUIEvent()
    }
}
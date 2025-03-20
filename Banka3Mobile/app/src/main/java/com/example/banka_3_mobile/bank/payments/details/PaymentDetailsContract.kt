package com.example.banka_3_mobile.bank.payments.details
import com.example.banka_3_mobile.bank.payments.details.model.PaymentDetailsGetResponse


interface PaymentDetailsContract {
    data class PaymentDetailsUiState(
        val payment: PaymentDetailsGetResponse? = null,
        val fetching: Boolean = true,
        val error: String? = null,
    )
}
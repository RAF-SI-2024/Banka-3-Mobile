package com.example.banka_3_mobile.bank.payments.new_payment

import com.example.banka_3_mobile.bank.model.AccountGetResponse

interface NewPaymentContract {
    data class NewPaymentUiState(
        val accounts: List<AccountGetResponse> = emptyList(),
        val fromAccount: String = "",
        val toAccount: String = "",
        val amount: Int = 0,
        val purpose: String = "",
        val paymentCode: String = "",
        val referenceNumber: String? = null,
        val error: String? = null,
        val paymentSuccess: Boolean = false,
        val purposeError: Boolean = false,
        val amountError: Boolean = false,
        val toAccountError: Boolean = false,
        val paymentCodeError: Boolean = false,
        val purposeErrorText: String = "",
        val amountErrorText: String = "",
        val toAccountErrorText: String = "",
        val paymentCodeErrorText: String = "",
        val invalidForm: Boolean = true,
    )

    sealed class NewPaymentUiEvent {
        data class TypingToAccount(val toAccountNumber: String): NewPaymentUiEvent()
        data class TypingAmount(val amount: Float): NewPaymentUiEvent()
        data class TypingPaymentCode(val paymentCode: String): NewPaymentUiEvent()
        data class TypingPaymentPurpose(val paymentPurpose: String): NewPaymentUiEvent()
        data class TypingReferenceNumber(val referenceNumber: String): NewPaymentUiEvent()
        data class FromAccountSelected(val fromAccount: String): NewPaymentUiEvent()
        data object SendPayment: NewPaymentUiEvent()
    }
}
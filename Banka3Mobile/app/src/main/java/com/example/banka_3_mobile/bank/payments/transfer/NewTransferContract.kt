package com.example.banka_3_mobile.bank.payments.transfer

import com.example.banka_3_mobile.bank.model.AccountGetResponse

interface NewTransferContract {
    data class NewTransferUiState(
        val accounts: List<AccountGetResponse> = emptyList(),
        val fromAccount: String = "",
        val toAccount: String = "",
        val amount: Int = 0,
        val error: String? = null,
        val convertedAmount: Float = 0f,
        val transferSuccess: Boolean = false,
        val amountError: Boolean = false,
        val amountErrorText: String = "",
        val invalidFormError: Boolean =  true
    )

    sealed class NewTransferUiEvent {
        data class ToAccountSelected(val toAccountNumber: String): NewTransferUiEvent ()
        data class TypingAmount(val amount: Float): NewTransferUiEvent ()
        data class FromAccountSelected(val fromAccount: String): NewTransferUiEvent ()
        data object SendTransaction: NewTransferUiEvent ()
        data object CloseTransactionDialog: NewTransferUiEvent()
        data class ConvertTransferAmount(val amount: Int): NewTransferUiEvent()
    }
}
package com.example.banka_3_mobile.bank.payments.ips.create

import com.example.banka_3_mobile.bank.model.AccountGetResponse

interface IPSCreateContract {
    data class IpsCreateUiState(
        val accounts: List<AccountGetResponse> = emptyList(),
        val accountName: String = "",
        val toAccount: String = "",
        val amount: Float? = null,
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
        val generatedQrCode: Boolean = false,
        val qrText: String? = null,
        val selectedAccountCurrencyCode: String = "RSD",
    )

    sealed class IpsCreateUiEvent {
        data class TypingAmount(val amount: Float?): IpsCreateUiEvent()
        data class TypingPaymentCode(val paymentCode: String): IpsCreateUiEvent()
        data class TypingPaymentPurpose(val paymentPurpose: String): IpsCreateUiEvent()
        data class TypingReferenceNumber(val referenceNumber: String): IpsCreateUiEvent()
        data class ToAccountSelected(val toAccount: String): IpsCreateUiEvent()
        data object CreateIPSQRCode: IpsCreateUiEvent()
    }
}
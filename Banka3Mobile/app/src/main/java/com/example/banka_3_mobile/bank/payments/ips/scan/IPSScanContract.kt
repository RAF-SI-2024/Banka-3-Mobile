package com.example.banka_3_mobile.bank.payments.ips.scan

import com.example.banka_3_mobile.bank.model.AccountGetResponse
import com.example.banka_3_mobile.bank.payments.ips.model.IPSModel

interface IPSScanContract {
    data class IPSScanUiState(
        val mockIPS: IPSModel = IPSModel(
            accountNumber = "222222222222222222",
            recipientName = "Marko Markovic",
           // amount = "EUR11,0",
            paymentCode = "123",
            purpose = "Pulpa"
        ),
        val scannedQRCode: String? = null,
        val scanCompleted: Boolean = false, // set to false for not-test
        val accounts: List<AccountGetResponse> = emptyList(),
        val fromAccount: String = "",
        val amount: Int = 0,
        val error: String? = null,
        val amountErrorText: String = "",
        val amountError: Boolean = false,
        val invalidForm: Boolean = false,
        val scannedIPS: IPSModel? = null, // set to null for non-test
        val paymentSuccess: Boolean = false,

    )

    sealed class IPSScanUiEvent {
        data class QRCodeScanned(val content: String) : IPSScanUiEvent()
        data class FromAccountSelected(val fromAccount: String): IPSScanUiEvent()
        data object SendPayment: IPSScanUiEvent()
        data class TypingAmount(val amount: Float): IPSScanUiEvent()
        data object CloseDialogAndNavigate : IPSScanUiEvent()
    }
}
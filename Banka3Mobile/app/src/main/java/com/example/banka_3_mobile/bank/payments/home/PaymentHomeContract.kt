package com.example.banka_3_mobile.bank.payments.home

import com.example.banka_3_mobile.bank.payments.home.model.ExchangeRateGetResponse

interface PaymentHomeContract {
    data class PaymentHomeUiState(
        val exchangeRates: List<ExchangeRateGetResponse> = emptyList(),
        val error: String? = null,
        val exchangeValue: Float = 0.0f,
        val exchangeInput: Int = 0,
        val showExchangeRatesDialog: Boolean = false,
        val showConversionDialog: Boolean = false,
        val availableSymbols: List<String> = emptyList(),
        val selectedToExchangeRate: String = "RSD",
        val selectedFromExchangeRate: String = "EUR",
    )

    sealed class PaymentHomeUiEvent {
        data class ConvertValue(val exchangeInput: Int): PaymentHomeUiEvent()
        data object OpenExchangeRateDialog: PaymentHomeUiEvent()
        data object CloseExchangeRateDialog: PaymentHomeUiEvent()
        data object OpenConversionDialog: PaymentHomeUiEvent()
        data object CloseConversionDialog: PaymentHomeUiEvent()
        data class ToSymbolSelected(val symbol: String): PaymentHomeUiEvent()
        data class FromSymbolSelected(val symbol: String): PaymentHomeUiEvent()
    }
}

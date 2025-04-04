package com.example.banka_3_mobile.bank.cards.details

import com.example.banka_3_mobile.bank.cards.model.CardsGetResponse
import com.example.banka_3_mobile.bank.payments.model.PaymentGetResponse

interface CardDetailsContract {
    data class CardDetailsUiState(
        val accountNumber: String,
        val cardNumber: String,
        val payments: List<PaymentGetResponse> = emptyList(),
        val card: CardsGetResponse? = null,
        val error: String? = null,
        val fetching: Boolean = true,
    )
}
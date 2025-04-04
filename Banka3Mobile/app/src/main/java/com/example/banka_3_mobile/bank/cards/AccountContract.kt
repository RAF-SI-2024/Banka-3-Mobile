package com.example.banka_3_mobile.bank.cards

import com.example.banka_3_mobile.bank.cards.model.CardsGetResponse
import com.example.banka_3_mobile.bank.model.AccountGetResponse
import com.example.banka_3_mobile.bank.payments.model.PaymentGetResponse

interface AccountContract {
    data class CardsUiState(
        val accountNumber: String,
        val accounts: List<AccountGetResponse> = emptyList(),
        val currentAccount: AccountGetResponse? = null,
        val fetching: Boolean = true,
        val cardList: List<CardsGetResponse> = emptyList(),
        val error: String? = null,
        val payments: List<PaymentGetResponse> = emptyList()
    )
}
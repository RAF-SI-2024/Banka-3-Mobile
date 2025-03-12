package com.example.banka_3_mobile.bank.repository

import com.example.banka_3_mobile.bank.api.BankApi
import com.example.banka_3_mobile.bank.payments.PaymentGetResponse
import com.example.banka_3_mobile.bank.payments.PaymentPageResponse
import javax.inject.Inject

class BankRepository @Inject constructor(
    private val bankApi: BankApi
) {

    suspend fun getPayments(): PaymentPageResponse {
        return bankApi.getPayments()
    }
}
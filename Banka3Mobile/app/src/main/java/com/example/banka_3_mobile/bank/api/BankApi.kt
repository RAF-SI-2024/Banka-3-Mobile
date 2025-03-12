package com.example.banka_3_mobile.bank.api

import com.example.banka_3_mobile.bank.payments.PaymentGetResponse
import com.example.banka_3_mobile.bank.payments.PaymentPageResponse
import retrofit2.http.GET

interface BankApi {
    @GET("payment")
    suspend fun getPayments(): PaymentPageResponse

    /**
     * TODO za verification request
     */

}
package com.example.banka_3_mobile.bank.api

import com.example.banka_3_mobile.bank.model.AccountGetResponse
import com.example.banka_3_mobile.bank.payments.PaymentGetResponse
import com.example.banka_3_mobile.bank.payments.PaymentPageResponse
import com.example.banka_3_mobile.verification.model.VerificationRequest
import retrofit2.http.GET

interface BankApi {
    @GET("payment")
    suspend fun getPayments(): PaymentPageResponse

    @GET("account")
    suspend fun getAccounts(): AccountGetResponse
}
package com.example.banka_3_mobile.bank.api

import com.example.banka_3_mobile.bank.cards.model.CardsGetResponse
import com.example.banka_3_mobile.bank.loans.details.model.LoanDetailsGetResponse
import com.example.banka_3_mobile.bank.loans.details.model.LoanInstallmentsGetResponse
import com.example.banka_3_mobile.bank.loans.model.LoanRequestPageResponse
import com.example.banka_3_mobile.bank.loans.model.LoanShortPageResponse
import com.example.banka_3_mobile.bank.model.AccountGetResponse
import com.example.banka_3_mobile.bank.payments.details.model.PaymentDetailsGetResponse
import com.example.banka_3_mobile.bank.payments.home.model.ExchangeRateGetResponse
import com.example.banka_3_mobile.bank.payments.model.PaymentPageResponse
import com.example.banka_3_mobile.bank.payments.new_payment.model.NewPaymentPostRequest
import com.example.banka_3_mobile.bank.payments.payee.model.PayeeGetResponse
import com.example.banka_3_mobile.bank.payments.transfer.model.NewTransferPostRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BankApi {
    @GET("payment")
    suspend fun getPayments(@Query("size") size: Int): PaymentPageResponse

    @GET("payment")
    suspend fun getPaymentsByAccount(
        @Query("accountNumber") accountNumber: String,
        @Query("size") size: Int
    ): PaymentPageResponse

    @GET("account")
    suspend fun getAccounts(): List<AccountGetResponse>

    @GET("account/{accountNumber}/cards/my-cards")
    suspend fun getCardsOfAccount(@Path("accountNumber") accountNumber: String): List<CardsGetResponse>

    @GET("exchange-rates")
    suspend fun getExchangeRates(): List<ExchangeRateGetResponse>

    @GET("exchange-rates/{fromCurrencyCode}/{toCurrencyCode}")
    suspend fun convertCurrency(
        @Path("fromCurrencyCode") fromCurrencyCode: String,
        @Path("toCurrencyCode") toCurrencyCode: String
    ): ExchangeRateGetResponse

    @POST("payment")
    suspend fun sendPayment(
        @Body request: NewPaymentPostRequest
    ): Response<Unit>

    @POST("payment/transfer")
    suspend fun sendTransfer(
        @Body request: NewTransferPostRequest
    ): Response<Unit>

    @GET("loans")
    suspend fun getLoans(): LoanShortPageResponse

    @GET("loans/{id}/installments")
    suspend fun getInstallmentsForLoan(
        @Path("id") id: Long
    ): List<LoanInstallmentsGetResponse>

    @GET("loans/{id}")
    suspend fun getLoanDetails(
        @Path("id") id: Long
    ): LoanDetailsGetResponse

    @GET("loan-requests")
    suspend fun getLoanRequests(): LoanRequestPageResponse

    @GET("payment/{id}")
    suspend fun getPaymentDetails(
        @Path("id") id: Long
    ): PaymentDetailsGetResponse

    @GET("payees/client")
    suspend fun getPayees(): List<PayeeGetResponse>

    @POST("payees")
    suspend fun addPayee(
        @Body request: PayeeGetResponse
    ): Response<Unit>

    @PUT("payees/{id}")
    suspend fun updatePayee(
        @Path("id") id: Long,
        @Body request: PayeeGetResponse,
    ): Response<Unit>

    @DELETE("payees/{id}")
    suspend fun deletePayee(
        @Path("id") id: Long
    ): Response<Unit>

}
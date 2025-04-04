package com.example.banka_3_mobile.bank.repository

import com.example.banka_3_mobile.bank.api.BankApi
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
import javax.inject.Inject

class BankRepository @Inject constructor(
    private val bankApi: BankApi
) {

    suspend fun getPayments(size: Int): PaymentPageResponse {
        return bankApi.getPayments(size)
    }

    suspend fun getPaymentsByAccount(accountNumber: String, size: Int): PaymentPageResponse {
        return bankApi.getPaymentsByAccount(accountNumber, size)
    }

    suspend fun getAccounts(): List<AccountGetResponse> {
        return bankApi.getAccounts()
    }

    suspend fun getCardsOfAccount(accountNumber: String): List<CardsGetResponse> {
        return bankApi.getCardsOfAccount(accountNumber)
    }

    suspend fun getExchangeRates(): List<ExchangeRateGetResponse> {
        return bankApi.getExchangeRates()
    }

    suspend fun convertCurrency(fromCurrencyCode: String, toCurrencyCode: String): ExchangeRateGetResponse {
        return bankApi.convertCurrency(fromCurrencyCode, toCurrencyCode)
    }

    suspend fun sendPayment(payment: NewPaymentPostRequest): Boolean {
        val response = bankApi.sendPayment(payment)
        return response.isSuccessful
    }
    suspend fun sendTransfer(transfer: NewTransferPostRequest): Boolean {
        val response = bankApi.sendTransfer(transfer)
        return response.isSuccessful
    }

    suspend fun getLoans(): LoanShortPageResponse {
      return bankApi.getLoans()
    }

    suspend fun getLoanDetails(id: Long): LoanDetailsGetResponse {
        return bankApi.getLoanDetails(id)
    }

    suspend fun getLoanInstallments(id: Long): List<LoanInstallmentsGetResponse> {
        return bankApi.getInstallmentsForLoan(id)
    }

    suspend fun getLoanRequests(): LoanRequestPageResponse {
        return bankApi.getLoanRequests()
    }

    suspend fun getPaymentDetails(id: Long): PaymentDetailsGetResponse {
        return bankApi.getPaymentDetails(id)
    }

    suspend fun getPayees(): List<PayeeGetResponse> {
        return bankApi.getPayees()
    }

    suspend fun deletePayee(id: Long): Boolean {
        val response = bankApi.deletePayee(id)
        return response.isSuccessful
    }

    suspend fun updatePayee(id: Long, request: PayeeGetResponse): Boolean {
        val response = bankApi.updatePayee(id, request)
        return response.isSuccessful
    }

    suspend fun addPayee(payee: PayeeGetResponse): Boolean {
        val response = bankApi.addPayee(payee)
        return response.isSuccessful
    }
}
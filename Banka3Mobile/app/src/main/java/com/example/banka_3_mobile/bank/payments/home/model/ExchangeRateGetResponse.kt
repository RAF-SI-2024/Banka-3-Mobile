package com.example.banka_3_mobile.bank.payments.home.model

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateGetResponse(
    val fromCurrency: CurrencyDto,
    val toCurrency: CurrencyDto,
    val exchangeRate: Float,
    val sellRate: Float
)

@Serializable
data class CurrencyDto(
    val code: String,
    val name: String,
    val symbol: String,
    val countries: String?,
    val description: String?,
    val active: Boolean
)

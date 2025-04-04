package com.example.banka_3_mobile.bank.portfolio.model

data class SecurityMock(
    val type: String,
    val ticker: String,
    val amount: Int,
    val price: Float,
    val profit: Float,
    val lastModified: String,
    val publicCounter: Int
)

val mockSecurities = listOf(
    SecurityMock(
        type = "Stock",
        ticker = "AAPL",
        amount = 50,
        price = 175.25f,
        profit = 1200.5f,
        lastModified = "2025-03-18",
        publicCounter = 5
    ),
    SecurityMock(
        type = "Stock",
        ticker = "TSLA",
        amount = 20,
        price = 220.75f,
        profit = -300f,
        lastModified = "2025-03-17",
        publicCounter = 8
    ),
    SecurityMock(
        type = "ETF",
        ticker = "SPY",
        amount = 100,
        price = 450.1f,
        profit = 2500f,
        lastModified = "2025-03-16",
        publicCounter = 0
    ),
    SecurityMock(
        type = "Crypto",
        ticker = "BTC",
        amount = 2,
        price = 68000f,
        profit = 15000.75f,
        lastModified = "2025-03-15",
        publicCounter = 20
    ),
    SecurityMock(
        type = "Bond",
        ticker = "US10Y",
        amount = 30,
        price = 98.5f,
        profit = 120.3f,
        lastModified = "2025-03-14",
        publicCounter = 0
    )
)

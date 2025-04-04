package com.example.banka_3_mobile.bank.portfolio

import com.example.banka_3_mobile.bank.portfolio.model.SecurityMock
import com.example.banka_3_mobile.bank.portfolio.model.mockSecurities
import com.example.banka_3_mobile.verification.model.VerificationRequest

interface MyPortfolioContract {
    data class MyPortfolioUiState(
        val securities: List<SecurityMock> = mockSecurities,
        val fetching: Boolean = true,
        val error: String? = null,
        val showProfitInfoDialog: Boolean = false,
        val showTaxInfoDialog: Boolean = false,
    )

    sealed class MyPortfolioUIEvent {
        data object OpenProfitInfoDialog: MyPortfolioUIEvent()
        data object CloseProfitInfoDialog: MyPortfolioUIEvent()
        data object OpenTaxInfoDialog: MyPortfolioUIEvent()
        data object CloseTaxInfoDialog: MyPortfolioUIEvent()
    }
}
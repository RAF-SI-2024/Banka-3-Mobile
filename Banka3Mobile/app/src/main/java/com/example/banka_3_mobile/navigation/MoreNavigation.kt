package com.example.banka_3_mobile.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import com.example.banka_3_mobile.bank.loans.details.loanDetailsPage
import com.example.banka_3_mobile.bank.loans.loansPage
import com.example.banka_3_mobile.bank.more.morePage
import com.example.banka_3_mobile.bank.portfolio.myPortfolioPage

fun NavGraphBuilder.moreNavigation(
    route: String,
    navController: NavController,
) = navigation(
    route = route,
    startDestination = "more/home",
) {

    morePage(
        route = "more/home",
        navController = navController
    )

    loansPage(
        route = "more/loans",
        navController = navController
    )

    loanDetailsPage(
        route = "more/loans/{loanId}",
        navController = navController
    )

    myPortfolioPage(
        route = "portfolio",
        navController = navController
    )

}

inline val SavedStateHandle.loanId: String
    get() = checkNotNull(get("loanId")) { "loanId is mandatory" }
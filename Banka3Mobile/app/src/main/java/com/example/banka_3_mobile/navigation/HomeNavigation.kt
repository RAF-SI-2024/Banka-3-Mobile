package com.example.banka_3_mobile.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.banka_3_mobile.bank.cards.cardsPage
import com.example.banka_3_mobile.bank.cards.details.cardDetailsPage
import com.example.banka_3_mobile.home.homePage

fun NavGraphBuilder.homeNavigation(
    route: String,
    navController: NavController,
) = navigation(
    route = route,
    startDestination = "home",
) {

    homePage(
        route = "home",
        navController = navController,
    )

    cardsPage(
        route = "home/{accountNumber}/cards",
        navController = navController,
    )

    cardDetailsPage("home/{accountNumber}/cards/{cardNumber}", navController = navController,
        arguments = listOf(
            navArgument("accountNumber") { type = NavType.StringType },
            navArgument("cardNumber") { type = NavType.StringType }))

}

inline val SavedStateHandle.accountNumber: String
    get() = checkNotNull(get("accountNumber")) { "accountNumber is mandatory" }
inline val SavedStateHandle.cardNumber: String
    get() = checkNotNull(get("cardNumber")) { "cardNumber is mandatory" }
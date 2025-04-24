package com.example.banka_3_mobile.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import com.example.banka_3_mobile.bank.payments.details.paymentDetailsPage
import com.example.banka_3_mobile.bank.payments.home.paymentHomePage
import com.example.banka_3_mobile.bank.payments.ips.create.ipsCreate
import com.example.banka_3_mobile.bank.payments.ips.scan.ipsScan
import com.example.banka_3_mobile.bank.payments.new_payment.newPaymentPage
import com.example.banka_3_mobile.bank.payments.payee.payeePage
import com.example.banka_3_mobile.bank.payments.transfer.newTransferPage

fun NavGraphBuilder.paymentNavigation(
    route: String,
    navController: NavController,
) = navigation(
    route = route,
    startDestination = "payment/home",
) {

    paymentHomePage(
        route = "payment/home",
        navController = navController,
    )

    newPaymentPage(
        route = "payment/new",
        navController = navController
    )

    newTransferPage(
        route = "transfer/new",
        navController = navController
    )

    paymentDetailsPage(
        route = "payment/{paymentId}",
        navController = navController
    )

    payeePage(
        route = "payee",
        navController = navController
    )

    ipsCreate(
        route = "ips/create",
        navController = navController,
    )

    ipsScan(
        route = "ips/scan",
        navController = navController,
    )
}

inline val SavedStateHandle.paymentId: String
    get() = checkNotNull(get("paymentId")) { "paymentId is mandatory" }
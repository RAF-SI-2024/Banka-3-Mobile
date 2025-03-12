package com.example.banka_3_mobile.navigation

import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.banka_3_mobile.home.homePage
import com.example.banka_3_mobile.login.loginPage
import com.example.banka_3_mobile.verification.verificationPage

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = {
            slideInHorizontally(
                animationSpec = spring(),
                initialOffsetX = { it },
            )
        },
        exitTransition = { scaleOut(targetScale = 0.75f) },
        popEnterTransition = { scaleIn(initialScale = 0.75f) },
        popExitTransition = { slideOutHorizontally { it } },
    ) {

        loginPage(route = "login",
            navController = navController)
        homePage(route = "home", navController = navController)
        verificationPage(route = "verify", navController = navController)
    }
}
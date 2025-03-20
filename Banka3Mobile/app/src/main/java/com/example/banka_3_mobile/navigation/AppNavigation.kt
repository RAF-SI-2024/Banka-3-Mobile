package com.example.banka_3_mobile.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.banka_3_mobile.bank.more.morePage
import com.example.banka_3_mobile.home.homePage
import com.example.banka_3_mobile.login.loginPage
import com.example.banka_3_mobile.verification.verificationPage

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val appNavigationViewModel: AppNavigationViewModel= hiltViewModel()
    val state by appNavigationViewModel.state.collectAsState()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "login") {
                BottomNavigationBar(
                    navController = navController,
                    activeRequestsCount = state.activeRequestCount
                )
            }
        }
    ) {
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
            homeNavigation(route = "user", navController = navController)
            paymentNavigation(route="payment", navController = navController)
            verificationPage(route = "verify", navController = navController)
            moreNavigation(route = "more", navController = navController)
        }
    }

}


@Composable
fun BottomNavigationBar(navController: NavController, activeRequestsCount: Int) {
    val navItems = listOf(
        NavItem.Home,
        NavItem.Payments,
        NavItem.Verification,
        NavItem.More
    )

    NavigationBar {
        navItems.forEach { item ->
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                   /* if (item is NavItem.Verification) {
                        *//**
                         * TODO clear
                         *//*
                    }*/
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                },
                icon = {
                    /*Box {
                        Icon(
                            imageV
                            ector = item.icon,
                            contentDescription = item.label
                        )
                        if (item is NavItem.Verification && activeRequestsCount > 0) {
                            Badge(modifier = Modifier.align(Alignment.TopEnd)) {
                                Text(text = activeRequestsCount.toString())
                            }
                        }
                    }*/
                   //Box {
                    BadgedBox(badge = {
                        if (item is NavItem.Verification && activeRequestsCount > 0) {
                            Badge {
                                //Text("$activeRequestsCount")
                            }
                        }
                    }) {

                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.width(32.dp).height(32.dp)
                        )
                    }
                },
                label = {
                    Text(text = item.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            )
        }
    }
}

sealed class NavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : NavItem("user", "Home", Icons.Default.Home)
    object Payments : NavItem("payment", "Payments", Icons.Default.AttachMoney)
    object Verification : NavItem("verify", "Verifications", Icons.Default.Notifications)
    object More: NavItem("more", "More", Icons.Default.MoreVert)
}
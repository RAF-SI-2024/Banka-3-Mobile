package com.example.banka_3_mobile.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.banka_3_mobile.login.LoginContract

fun NavGraphBuilder.homePage(
    route: String,
    navController: NavController,
) = composable(
    route = route,
) {navBackStackEntry ->

    val homeViewModel = hiltViewModel<HomeViewModel>(navBackStackEntry)
    val state by homeViewModel.state.collectAsState()

    LaunchedEffect(!state.loggedIn) {
        if (!state.loggedIn) {
            navController.navigate("login") {
                popUpTo(route) { inclusive = true }
            }
        }
    }

    HomeScreen(
        state = state,
        eventPublisher = {
            homeViewModel.setEvent(it)
        },
        onVerify = {
            navController.navigate("verify")
        }
    )
}

@Composable
fun HomeScreen(
    state: HomeContract.HomeUiState,
    eventPublisher: (uiEvent: HomeContract.HomeUIEvent) -> Unit,
    onVerify: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        content = {

            when {
                    state.fetching ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    state.error != null ->
                        Text(color = Color.Red, text = "${state.error}")
                    else -> {
                        HomeColumn(state = state, eventPublisher = eventPublisher, onVerify = onVerify)
                    }


            }
        })
}

@Composable
fun HomeColumn (
    state: HomeContract.HomeUiState,
    eventPublisher: (uiEvent: HomeContract.HomeUIEvent) -> Unit,
    onVerify: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp).padding(top = 16.dp),
    ) {
        HomeHeader(
            state = state,
            eventPublisher = eventPublisher,
            onVerify = onVerify
        )
    }
}

@Composable
fun HomeHeader(
    state: HomeContract.HomeUiState,
    eventPublisher: (HomeContract.HomeUIEvent) -> Unit,
    onVerify: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp).padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${state.client?.firstName.orEmpty()} ${state.client?.lastName.orEmpty()}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = state.client?.username.orEmpty(),
                fontSize = 18.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color.Gray
            )
            Text(
                text = state.client?.email.orEmpty(),
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
        Button(onClick = { eventPublisher(HomeContract.HomeUIEvent.ShowLogoutDialog) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
            Text("Logout", style = MaterialTheme.typography.bodyLarge, color=MaterialTheme.colorScheme.onSecondary)
        }
    }
    Button(onClick = {
        onVerify()},
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
        Text(text = "Verify", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium)
    }

    if (state.showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                eventPublisher(HomeContract.HomeUIEvent.Logout)
            },
            onDismiss = {
                eventPublisher(HomeContract.HomeUIEvent.CloseLogoutDialog)
            }
        )
    }
}



@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Logout") },
        text = { Text("Are you sure you want to log out?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}
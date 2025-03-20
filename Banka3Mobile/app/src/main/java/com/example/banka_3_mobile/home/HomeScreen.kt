package com.example.banka_3_mobile.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.banka_3_mobile.bank.model.AccountGetResponse
import com.example.banka_3_mobile.bank.model.AccountStatus
import com.example.banka_3_mobile.verification.NavHeader

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
        },
        onAccountClick = { accountNumber ->
            navController.navigate("home/$accountNumber/cards")
        }
    )
}

@Composable
fun HomeScreen(
    state: HomeContract.HomeUiState,
    eventPublisher: (uiEvent: HomeContract.HomeUIEvent) -> Unit,
    onVerify: () -> Unit,
    onAccountClick: (accountNumber: String) -> Unit
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
                        Text(color = MaterialTheme.colorScheme.error, text = "${state.error}")
                    else -> {
                        HomeColumn(state = state, eventPublisher = eventPublisher, onVerify = onVerify,
                            onAccountClick = onAccountClick)
                    }


            }
        })
}

@Composable
fun HomeColumn (
    state: HomeContract.HomeUiState,
    eventPublisher: (uiEvent: HomeContract.HomeUIEvent) -> Unit,
    onVerify: () -> Unit,
    onAccountClick: (accountNumber: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize().padding(bottom = 80.dp)
            //.padding(horizontal = 24.dp)//.padding(top = 16.dp),
    ) {
        //HomeHeader()
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).padding(top = 32.dp)) {
                NavHeader("Home")
            }
        }
        item {
            UserInfoItem(
                state = state,
                eventPublisher = eventPublisher,
                onVerify = onVerify
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            if (state.userAccounts.isNotEmpty()) {
                Column (
                    //  horizontalAlignment = Alignment.CenterHorizontally,
                    //      verticalArrangement = Arrangement.Center
                ) {
                    Text("Your Accounts", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 16.dp))
                    // AccountsList(accounts = state.userAccounts, onAccountClick = onAccountClick)
                    AccountsCarousel(state.userAccounts, onAccountClick = onAccountClick)
                }
            } else {
                Text(
                    text = "No accounts available",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

    }
}

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            //.height(56.dp)
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 16.dp, vertical = 16.dp).padding(top = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = "Bank Icon",
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.height(32.dp).width(32.dp)
        )
        Text(
            text = "Banka-3",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}


@Composable
fun UserInfoItem(
    state: HomeContract.HomeUiState,
    eventPublisher: (HomeContract.HomeUIEvent) -> Unit,
    onVerify: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),//.padding(top = 16.dp),
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
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
            Text("Logout", style = MaterialTheme.typography.bodyLarge, color=MaterialTheme.colorScheme.onSecondary)
        }
    }
    /*Button(onClick = {
        onVerify()},
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
        Text(text = "Verify", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium)
    }*/

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

/*@Composable
fun AccountsList(
    accounts: List<AccountGetResponse>,
    onAccountClick: (accountNumber: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(accounts) { account ->
            AccountCard(account = account, onAccountClick = onAccountClick)
        }
    }
}


@Composable
fun AccountCard(
    account: AccountGetResponse,
    modifier: Modifier = Modifier,
    onAccountClick: (accountNumber: String) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp).clickable {
                onAccountClick(account.accountNumber)
            }
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.accountNumber,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Available Balance: ${account.availableBalance} ${account.currencyCode}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = account.status.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (account.status == AccountStatus.ACTIVE) Color.Green else MaterialTheme.colorScheme.error
            )
        }
    }
}*/


@Composable
fun AccountsCarousel(
    accounts: List<AccountGetResponse>,
    onAccountClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
    ) {
        items(accounts) { account ->
            AccountCardCarousel(account = account, onClick = { onAccountClick(account.accountNumber) })
        }
    }
}

@Composable
fun AccountCardCarousel(
    account: AccountGetResponse,
    onClick: () -> Unit
) {
    val ownershipEnglish = when (account.ownershipType.lowercase()) {
        "licni" -> "Personal"
        "poslovni" -> "Business"
        else -> account.ownershipType
    }
    val categoryEnglish = when (account.accountCategory.lowercase()) {
        "tekuci" -> "Checking"
        "devizni" -> "Foreign"
        else -> account.accountCategory
    }

    Card(
        modifier = Modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = "Account Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .width(56.dp)
                    .height(56.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Column {
                    Text(
                        text = ownershipEnglish,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = categoryEnglish,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                // Spacer(modifier = Modifier.height(8.dp))
                // Account number
                Text(
                    text = account.accountNumber,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Balance
                Text(
                    text = "${account.balance} ${account.currencyCode}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
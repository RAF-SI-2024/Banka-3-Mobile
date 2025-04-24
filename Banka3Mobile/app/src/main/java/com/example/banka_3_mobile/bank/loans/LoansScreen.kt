package com.example.banka_3_mobile.bank.loans

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.loansPage(
    route: String,
    navController: NavController,
) = composable(route = route) { navBackStackEntry ->
    val loansViewModel = hiltViewModel<LoansViewModel>(navBackStackEntry)
    val state by loansViewModel.state.collectAsState()
    LoansScreen(
        state = state,
        onClose = { navController.navigateUp() },
        onLoanClick = { loanId ->
            navController.navigate("more/loans/$loanId")
        }
    )
}

@Composable
fun LoansScreen(
    state: LoansContract.LoansUiState,
    onClose: () -> Unit,
    onLoanClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp).padding(top = 32.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Your Loans",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (state.loans.isNotEmpty()) {
            items(state.loans) { loan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLoanClick(loan.id) },
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Loan Number:",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = loan.loanNumber,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Type: ${loan.type}",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = "Amount: ${loan.amount}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        } else {
            item {
                Text(
                    text = "No loans available.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Loan Requests",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (state.loanRequests.isNotEmpty()) {
            items(state.loanRequests) { request ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Type: ${request.type}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Amount: ${request.amount}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        } else {
            item {
                Text(
                    text = "No loan requests available.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

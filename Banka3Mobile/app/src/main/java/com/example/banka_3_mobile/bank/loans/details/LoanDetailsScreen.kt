package com.example.banka_3_mobile.bank.loans.details

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
import com.example.banka_3_mobile.bank.loans.details.model.LoanDetailsGetResponse
import com.example.banka_3_mobile.bank.loans.details.model.LoanInstallmentsGetResponse


fun NavGraphBuilder.loanDetailsPage(
    route: String,
    navController: NavController,
) = composable(route = route) { navBackStackEntry ->
    val loanDetailsViewModel = hiltViewModel<LoanDetailsViewModel>(navBackStackEntry)
    val state by loanDetailsViewModel.state.collectAsState()
    LoanDetailsScreen(
        state = state,
        onClose = { navController.navigateUp() },
    )
}


@Composable
fun LoanDetailsScreen(
    state: LoanDetailsContract.LoanDetailsUiState,
    onClose: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp).padding(top = 32.dp, bottom = 80.dp),
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
                    text = "Loan Details",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            if (state.loan != null) {
                LoanDetailsCard(loan = state.loan)
            } else {
                Text(
                    text = "No loan details available.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Loan Installments",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(state.loanInstallments) { installment ->
            InstallmentItem(installment = installment)
        }
    }
}

@Composable
fun LoanDetailsCard(loan: LoanDetailsGetResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            //LoanDetailRow(label = "Loan Number", value = loan.loanNumber)
            Text(text = loan.loanNumber, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 8.dp))
            LoanDetailRow(label = "Type", value = loan.type.toString())
            LoanDetailRow(label = "Amount", value = loan.amount.toString())
            LoanDetailRow(label = "Repayment Period", value = loan.repaymentPeriod.toString())
            LoanDetailRow(label = "Nominal Rate", value = loan.nominalInterestRate.toString())
            LoanDetailRow(label = "Effective Rate", value = loan.effectiveInterestRate.toString())
            LoanDetailRow(label = "Start Date", value = loan.startDate)
            LoanDetailRow(label = "Due Date", value = loan.dueDate)
            LoanDetailRow(label = "Next Installment", value = loan.nextInstallmentAmount.toString())
            LoanDetailRow(label = "Next Installment Date", value = loan.nextInstallmentDate)
            LoanDetailRow(label = "Remaining Debt", value = loan.remainingDebt.toString())
            LoanDetailRow(label = "Currency", value = loan.currencyCode)
            LoanDetailRow(label = "Status", value = loan.status.toString())
        }
    }
}

@Composable
fun InstallmentItem(installment: LoanInstallmentsGetResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            LoanDetailRow(label = "Amount", value = installment.amount.toString())
            LoanDetailRow(label = "Interest Rate", value = installment.interestRate.toString())
            LoanDetailRow(label = "Expected Due", value = installment.expectedDueDate)
            installment.actualDueDate?.let { LoanDetailRow(label = "Actual Due", value = it) }
            LoanDetailRow(label = "Status", value = installment.installmentStatus.toString())
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun LoanDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}
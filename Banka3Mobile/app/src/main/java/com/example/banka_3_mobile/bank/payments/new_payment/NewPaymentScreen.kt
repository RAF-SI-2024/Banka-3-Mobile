package com.example.banka_3_mobile.bank.payments.new_payment

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.banka_3_mobile.bank.model.AccountGetResponse


fun NavGraphBuilder.newPaymentPage(
    route: String,
    navController: NavController,
) = composable(
    route = route,
) {navBackStackEntry ->

    val newPaymentViewModel = hiltViewModel<NewPaymentViewModel>(navBackStackEntry)
    val state by newPaymentViewModel.state.collectAsState()
    NewPaymentScreen(
        state = state,
        eventPublisher = {
            newPaymentViewModel.setEvent(it)
        },
        onClose = {
            navController.navigateUp()
        }
    )
}


@Composable
fun NewPaymentScreen(
    state: NewPaymentContract.NewPaymentUiState,
    eventPublisher: (uiEvent: NewPaymentContract.NewPaymentUiEvent) -> Unit,
    onClose: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize().padding(bottom = 70.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NewPaymentHeader(onClose = onClose)
            if (state.accounts.isNotEmpty()) {
                FromAccountSelector(
                    accounts = state.accounts,
                    selectedAccount = state.fromAccount,
                    onAccountSelected = { accountNumber ->
                        eventPublisher(NewPaymentContract.NewPaymentUiEvent.FromAccountSelected(accountNumber))
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            OutlinedTextField(
                value = state.toAccount,
                onValueChange = { eventPublisher(NewPaymentContract.NewPaymentUiEvent.TypingToAccount(it)) },
                label = { Text("Receiver Account Number") },
                placeholder = { Text("Enter receiver account number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            if (state.toAccountError) {
                Text(state.toAccountErrorText, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = if (state.amount == 0) "" else state.amount.toString(),
                onValueChange = { input ->
                    eventPublisher(
                        NewPaymentContract.NewPaymentUiEvent.TypingAmount(
                            input.toFloatOrNull() ?: 0f
                        )
                    )
                },
                label = { Text("Amount") },
                placeholder = { Text("Enter amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            if (state.amountError) {
                Text(state.amountErrorText, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.paymentCode,
                onValueChange = { eventPublisher(NewPaymentContract.NewPaymentUiEvent.TypingPaymentCode(it)) },
                label = { Text("Payment Code") },
                placeholder = { Text("Enter payment code") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            if (state.paymentCodeError) {
                Text(state.paymentCodeErrorText, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.purpose,
                onValueChange = { eventPublisher(NewPaymentContract.NewPaymentUiEvent.TypingPaymentPurpose(it)) },
                label = { Text("Purpose") },
                placeholder = { Text("Enter purpose of payment") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            if (state.purposeError) {
                Text(state.purposeErrorText, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.referenceNumber ?: "",
                onValueChange = { eventPublisher(NewPaymentContract.NewPaymentUiEvent.TypingReferenceNumber(it)) },
                label = { Text("Reference Number (optional)") },
                placeholder = { Text("Enter reference number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (state.error!=null) {
                Text(state.error, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(24.dp))
            }
            Button(
                onClick = { eventPublisher(NewPaymentContract.NewPaymentUiEvent.SendPayment) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Send Payment", fontSize = 18.sp)
            }

        }
        if (state.paymentSuccess) {
            AlertDialog(
                onDismissRequest = {
                    onClose()
                },
                title = { Text(text = "Payment created successfully") },
                confirmButton = {
                    Button(
                        onClick = { onClose() }
                    ) {
                        Text(text = "Close")
                    }
                }
            )
        }
    }
}


@Composable
fun FromAccountSelector(
    accounts: List<AccountGetResponse>,
    selectedAccount: String,
    onAccountSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { expanded = true }
    ) {
        OutlinedTextField(
            value = selectedAccount,
            onValueChange = {},
            label = { Text("From Account") },
            placeholder = { Text("Select your account") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            shape = RoundedCornerShape(8.dp)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text(account.accountNumber) },
                    onClick = {
                        onAccountSelected(account.accountNumber)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun NewPaymentHeader(
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
        Spacer(modifier = Modifier.width(30.dp))
        Text(
            text = "New Payment",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
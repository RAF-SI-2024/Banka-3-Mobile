package com.example.banka_3_mobile.bank.payments.transfer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.banka_3_mobile.bank.model.AccountGetResponse

fun NavGraphBuilder.newTransferPage(
    route: String,
    navController: NavController,
) = composable(route = route) { navBackStackEntry ->
    val newTransferViewModel = hiltViewModel<NewTransferViewModel>(navBackStackEntry)
    val state by newTransferViewModel.state.collectAsState()
    NewTransferScreen(
        state = state,
        eventPublisher = { newTransferViewModel.setEvent(it) },
        onClose = { navController.navigateUp() }
    )
}

@Composable
fun NewTransferScreen(
    state: NewTransferContract.NewTransferUiState,
    eventPublisher: (uiEvent: NewTransferContract.NewTransferUiEvent) -> Unit,
    onClose: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NewTransferHeader(onClose = onClose)
            Spacer(modifier = Modifier.height(16.dp))
            if (state.accounts.isNotEmpty()) {
                AccountSelector(
                    label = "From Account",
                    accounts = state.accounts,
                    selectedAccountNumber = state.fromAccount,
                    onAccountSelected = { accountNumber ->
                        eventPublisher(NewTransferContract.NewTransferUiEvent.FromAccountSelected(accountNumber))
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                AccountSelector(
                    label = "To Account",
                    accounts = state.accounts,
                    selectedAccountNumber = state.toAccount,
                    onAccountSelected = { accountNumber ->
                        eventPublisher(NewTransferContract.NewTransferUiEvent.ToAccountSelected(accountNumber))
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            OutlinedTextField(
                value = if (state.amount == 0) "" else state.amount.toString(),
                onValueChange = { input ->
                    eventPublisher(
                        NewTransferContract.NewTransferUiEvent.TypingAmount(
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
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        eventPublisher(NewTransferContract.NewTransferUiEvent.ConvertTransferAmount(state.amount))
                    },
                    modifier = Modifier.height(50.dp)
                ) {
                    Text(text = "Convert", fontSize = 16.sp)
                }
                OutlinedTextField(
                    value = if (state.convertedAmount == 0f) "" else state.convertedAmount.toString(),
                    onValueChange = {},
                    label = { Text("Converted") },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { eventPublisher(NewTransferContract.NewTransferUiEvent.SendTransaction) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Send Transfer", fontSize = 18.sp)
            }
        }
    }
    if (state.transferSuccess) {
        AlertDialog(
            onDismissRequest = {
                eventPublisher(NewTransferContract.NewTransferUiEvent.CloseTransactionDialog)
                onClose() },
            title = { Text(text = "Transfer created successfully") },
            confirmButton = {
                Button(onClick = {
                    eventPublisher(NewTransferContract.NewTransferUiEvent.CloseTransactionDialog)
                    onClose()
                }) {
                    Text(text = "Close")
                }
            }
        )
    }
}

@Composable
fun NewTransferHeader(
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
        Spacer(modifier = Modifier.width(30.dp))
        Text(
            text = "New Transfer",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun AccountSelector(
    label: String,
    accounts: List<AccountGetResponse>,
    selectedAccountNumber: String,
    onAccountSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedAccount = accounts.find { it.accountNumber == selectedAccountNumber }
    val displayText = if (selectedAccount != null)
        "${selectedAccount.accountNumber} (${selectedAccount.currencyCode})"
    else ""
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { expanded = true }
    ) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            label = { Text(label) },
            placeholder = { Text("Select your account") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            shape = RoundedCornerShape(8.dp)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text("${account.accountNumber} (${account.currencyCode})") },
                    onClick = {
                        onAccountSelected(account.accountNumber)
                        expanded = false
                    }
                )
            }
        }
    }
}

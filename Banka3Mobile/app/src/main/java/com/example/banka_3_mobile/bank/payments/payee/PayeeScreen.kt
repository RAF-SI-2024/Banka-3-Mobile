package com.example.banka_3_mobile.bank.payments.payee

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.banka_3_mobile.bank.payments.payee.model.PayeeGetResponse


fun NavGraphBuilder.payeePage(
    route: String,
    navController: NavController,
) = composable(route = route) { navBackStackEntry ->
    val payeeViewModel = hiltViewModel<PayeeViewModel>(navBackStackEntry)
    val state by payeeViewModel.state.collectAsState()
    PayeeScreen(
        state = state,
        eventPublisher = {
            payeeViewModel.setEvent(it)
        },
        onClose = {
            navController.navigateUp()
        }
    )
}

@Composable
fun PayeeScreen(
    state: PayeeInterface.PayeeUiState,
    eventPublisher: (uiEvent: PayeeInterface.PayeeUiEvent) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).padding(top = 24.dp))
    {
        PayeeHeader(onClose = onClose)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { eventPublisher(PayeeInterface.PayeeUiEvent.OpenAddDialog) },
          //  modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Payee", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                if (state.payees.isNotEmpty()) {
                    Text(text="Your Payment Recipients", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                } else {
                    Text(text = "You have no Payment Recipients.", style = MaterialTheme.typography.bodyLarge)
                }

            }
            items(state.payees) { payee ->
                PayeeListItem(
                    payee = payee,
                    eventPublisher = eventPublisher
                )
            }
        }
    }
    if (state.addDialogOpen) {
        PayeeDialog(
            title = "Add Payee",
            name = state.nameField,
            accountNumber = state.accountNumberField,
            onNameChange = { eventPublisher(PayeeInterface.PayeeUiEvent.TypingName(it)) },
            onAccountNumberChange = { eventPublisher(PayeeInterface.PayeeUiEvent.TypingAccountNumber(it)) },
            onConfirm = { eventPublisher(PayeeInterface.PayeeUiEvent.AddPayee) },
            onDismiss = { eventPublisher(PayeeInterface.PayeeUiEvent.CloseAddDialog) },
            state = state
        )
    }
    if (state.editDialogOpen) {
        PayeeDialog(
            title = "Edit Payee",
            name = state.nameField,
            accountNumber = state.accountNumberField,
            onNameChange = { eventPublisher(PayeeInterface.PayeeUiEvent.TypingName(it)) },
            onAccountNumberChange = { eventPublisher(PayeeInterface.PayeeUiEvent.TypingAccountNumber(it)) },
            onConfirm = { eventPublisher(PayeeInterface.PayeeUiEvent.UpdatePayee) },
            onDismiss = { eventPublisher(PayeeInterface.PayeeUiEvent.CloseEditDialog) },
            state = state
        )
    }
    if (state.deleteDialogOpen) {
        PayeeDialog(
            title = "Delete Payee",
            name = state.nameField,
            accountNumber = state.accountNumberField,
            onNameChange = {},
            onAccountNumberChange = {},
            onConfirm = { eventPublisher(PayeeInterface.PayeeUiEvent.DeletePayee) },
            onDismiss = { eventPublisher(PayeeInterface.PayeeUiEvent.CloseDeleteDialog) },
            readOnly = true,
            state = state
        )
    }
}

@Composable
fun PayeeHeader(onClose: () -> Unit) {
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
            text = "Payment Recipients",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PayeeListItem(
    payee: PayeeGetResponse,
    eventPublisher: (uiEvent: PayeeInterface.PayeeUiEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = payee.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Text(text = payee.accountNumber, style = MaterialTheme.typography.bodyMedium)
        }
        Row {
            Button(
                onClick = { eventPublisher(PayeeInterface.PayeeUiEvent.OpenEditDialog(payee.id ?: -1)) },
                modifier = Modifier.padding(end = 4.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
            ) {
                Text("Edit", style = MaterialTheme.typography.titleMedium)
            }
            Button(
                onClick = { eventPublisher(PayeeInterface.PayeeUiEvent.OpenDeleteDialog(payee.id ?: -1)) },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)

            ) {
                Text("Delete", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun PayeeDialog(
    title: String,
    name: String,
    accountNumber: String,
    onNameChange: (String) -> Unit,
    onAccountNumberChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    readOnly: Boolean = false,
    state: PayeeInterface.PayeeUiState
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = readOnly
                )
                if (state.nameError) {
                    Text(state.nameErrorText, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = onAccountNumberChange,
                    label = { Text("Account Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    readOnly = readOnly
                )
                if (state.accountNumberError) {
                    Text(state.accountNumberErrorText, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge)
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)) {
                Text("Close")
            }
        }
    )
}

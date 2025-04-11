package com.example.banka_3_mobile.bank.payments.home

import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapCalls
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.banka_3_mobile.bank.payments.home.model.CurrencyDto
import com.example.banka_3_mobile.bank.payments.home.model.ExchangeRateGetResponse
import com.example.banka_3_mobile.verification.NavHeader


fun NavGraphBuilder.paymentHomePage(
    route: String,
    navController: NavController,
) = composable(
    route = route,
) {navBackStackEntry ->

    val paymentHomeViewModel = hiltViewModel<PaymentHomeViewModel>(navBackStackEntry)
    val state by paymentHomeViewModel.state.collectAsState()

    PaymentHomeScreen(state = state,
        eventPublisher = {
            paymentHomeViewModel.setEvent(it)
        },
        onNewPaymentClick = {
            navController.navigate("payment/new")
        },
        onNewTransferClick = {
            navController.navigate("transfer/new")
        },
        onPayeesClick = {
            navController.navigate("payee")
        }
        )
}


@Composable
fun PaymentHomeScreen(
    state: PaymentHomeContract.PaymentHomeUiState,
    eventPublisher: (uiEvent: PaymentHomeContract.PaymentHomeUiEvent) -> Unit,
    onNewPaymentClick: () -> Unit,
    onNewTransferClick: () -> Unit,
    onPayeesClick: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        content = {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(top = 24.dp)
            ) {
                NavHeader(title = "Payments")


                PaymentActionCard(onClick = onNewPaymentClick,
                    titleText = "New Payment",
                    bottomText = "Make a payment quickly.",
                    imageVector = Icons.Filled.Payments)
                PaymentActionCard(onClick = onNewTransferClick,
                    titleText = "New Transfer",
                    bottomText = "Transfer funds easily.",
                    imageVector = Icons.AutoMirrored.Filled.Send)
                PaymentActionCard(onClick = onPayeesClick,
                    titleText = "Your Payees",
                    bottomText = "Manage payment recipients.",
                    imageVector = Icons.Default.PersonPin)

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.OpenConversionDialog) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box {
                                Icon(
                                    imageVector = Icons.Default.CurrencyExchange,
                                    contentDescription = "Currency",
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Currency",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Converter",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.OpenExchangeRateDialog) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box {
                                Icon(
                                    imageVector = Icons.Default.SwapCalls,
                                    contentDescription = "Exchange Rates",
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Exchange",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Rates",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                }
            }
        })
    if (state.showExchangeRatesDialog) {
        /*AlertDialog(
            onDismissRequest = { eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.CloseExchangeRateDialog)},
            title = { Text("Exchange Rates") },
            text = { Text("Here are the current exchange rates (dummy data).") },
            confirmButton = {
                Button(onClick = { eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.CloseExchangeRateDialog) }) {
                    Text("Close")
                }
            }
        )*/
        ExchangeRatesDialog(
            state = state,
            eventPublisher = eventPublisher
        )
    }

    if (state.showConversionDialog) {
        CurrencyConversionDialog(state = state, eventPublisher = eventPublisher)
        /*AlertDialog(
            onDismissRequest = { eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.CloseConversionDialog) },
            title = { Text("Currency Converter") },
            text = {
                Text("Enter a value to convert (dummy implementation).")
            },
            confirmButton = {
                Button(onClick = { eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.CloseConversionDialog)  }) {
                    Text("Convert & Close")
                }
            },
            dismissButton = {
                Button(onClick = { eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.CloseConversionDialog)  }) {
                    Text("Cancel")
                }
            }
        )*/
    }
}

@Composable
fun ExchangeRatesDialog(
    state: PaymentHomeContract.PaymentHomeUiState,
    eventPublisher: (uiEvent: PaymentHomeContract.PaymentHomeUiEvent) -> Unit,
) {
    val filteredRates = state.exchangeRates.filter { it.toCurrency.code == state.selectedToExchangeRate }

    AlertDialog(
        onDismissRequest = {
            eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.CloseExchangeRateDialog)
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Exchange Rates", style = MaterialTheme.typography.titleLarge)
                SymbolSelector(
                    selectedSymbol = state.selectedToExchangeRate,
                    symbols = state.availableSymbols
                ) { newSymbol ->
                    eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.ToSymbolSelected(newSymbol))
                }
            }
        },
        text = {
            LazyColumn(modifier = Modifier.height(500.dp)) {
                items(filteredRates) { rate ->
                    ExchangeRateListItem(rate = rate)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.CloseExchangeRateDialog)
            }) {
                Text("Close")
            }
        }
    )
}


@Composable
fun SymbolSelector(
    selectedSymbol: String,
    symbols: List<String>,
    onSymbolSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clickable { expanded = !expanded }
            .wrapContentWidth()
            .wrapContentHeight()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = selectedSymbol,
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select Symbol",
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            symbols.forEach { symbol ->
                DropdownMenuItem(
                    text = { Text(symbol) },
                    onClick = {
                        onSymbolSelected(symbol)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun ExchangeRateListItem(rate: ExchangeRateGetResponse) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondary,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        ListItem(
            headlineContent = {},
            supportingContent = {
            },
            trailingContent = {
                Text(
                    text = "1 ${rate.fromCurrency.code} → ${"%.2f".format(rate.exchangeRate)} ${rate.toCurrency.code}",
                    style = MaterialTheme.typography.titleSmall
                )
            },
            leadingContent = {
                Text(text = rate.fromCurrency.code, style = MaterialTheme.typography.titleLarge)
            },
        )
    }
}

@Composable
fun PaymentActionCard(
    onClick: () -> Unit,
    titleText: String,
    bottomText: String,
    imageVector: ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bottomText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(
                imageVector = imageVector,
                contentDescription = titleText,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .height(32.dp)
                    .width(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun CurrencyConversionContent(
    state: PaymentHomeContract.PaymentHomeUiState,
    eventPublisher: (PaymentHomeContract.PaymentHomeUiEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = if (state.exchangeInput != 0) state.exchangeInput.toString() else "",
                onValueChange = { input ->
                    val newValue = input.toIntOrNull() ?: 0
                    eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.ConvertValue(newValue))
                },
                label = { Text("Amount", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)) },
                placeholder = { Text("Enter amount", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            SymbolSelector(
                selectedSymbol = state.selectedFromExchangeRate,
                symbols = state.availableSymbols,
                onSymbolSelected = { newSymbol ->
                    eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.FromSymbolSelected(newSymbol))
                    eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.ConvertValue(state.exchangeInput))
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = if (state.exchangeInput != 0) state.exchangeValue.toString() else "",
                onValueChange = {},
                label = { Text("Converted", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)) },
                readOnly = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
            SymbolSelector(
                selectedSymbol = state.selectedToExchangeRate,
                symbols = state.availableSymbols,
                onSymbolSelected = { newSymbol ->
                    eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.ToSymbolSelected(newSymbol))
                    eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.ConvertValue(state.exchangeInput))
                }
            )
        }
    }
}

@Composable
fun CurrencyConversionDialog(
    state: PaymentHomeContract.PaymentHomeUiState,
    eventPublisher: (PaymentHomeContract.PaymentHomeUiEvent) -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.CloseConversionDialog)
        },
        title = { Text("Currency Converter") },
        text = {
            CurrencyConversionContent(state = state, eventPublisher = eventPublisher)
        },
        confirmButton = {
            Button(
                onClick = {
                    eventPublisher(PaymentHomeContract.PaymentHomeUiEvent.CloseConversionDialog)
                }
            ) {
                Text("Close")
            }
        },
    )
}

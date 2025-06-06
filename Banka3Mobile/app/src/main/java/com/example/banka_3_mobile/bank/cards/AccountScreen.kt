package com.example.banka_3_mobile.bank.cards

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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.banka_3_mobile.bank.payments.model.PaymentGetResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun NavGraphBuilder.cardsPage(
    route: String,
    navController: NavController,
) = composable(
    route = route,
) {navBackStackEntry ->

    val cardsViewModel = hiltViewModel<AccountViewModel>(navBackStackEntry)
    val state by cardsViewModel.state.collectAsState()

    AccountScreen(
        state = state,
        onClose = {
            navController.navigateUp()
        },
        onCardClick = { cardNumber ->
            navController.navigate("home/${state.accountNumber}/cards/$cardNumber")
        },
        onPaymentClick = { paymentId ->
            navController.navigate("payment/$paymentId")
        }
    )
}

@Composable
fun AccountScreen(
    onClose: () -> Unit,
    state: AccountContract.CardsUiState,
    onCardClick: (cardNumber: String) -> Unit,
    onPaymentClick: (paymentId: String) -> Unit
) {
    var detailsVisible by remember { mutableStateOf(false) }
    val filteredCards = state.cardList.filter { it.accountNumber == state.accountNumber }
    Surface(
        modifier = Modifier.fillMaxSize().padding(bottom = 60.dp),
        content = {
            Column  (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                CardsHeader(
                    onClose = onClose,
                    state = state,
                    onToggleVisibility = { detailsVisible = !detailsVisible },
                    detailsVisible = detailsVisible
                )
                if (state.cardList.isEmpty()) {
                    Text(
                        text = "No cards available for this account.",
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            if (filteredCards.isNotEmpty()) {
                                Text(
                                    text = "Your cards",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            } else {
                                Text(
                                    text = "You have no cards tied to this account.",
                                    style = MaterialTheme.typography.titleSmall,
                                    //fontWeight = FontWeight.Bold
                                )
                            }

                        }
                        items(filteredCards) { card ->
                            CreditCardItem(
                                cardNumber = card.cardNumber,
                                cardHolder = "${card.owner.firstName} ${card.owner.lastName}",
                                expirationDate = card.expirationDate,
                                onCardClick = onCardClick,
                                detailsVisible = detailsVisible
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            if (state.payments.isNotEmpty()) {
                                Text(
                                    text = "Payments",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                Text(
                                    text = "No payments recorded for this account.",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                        }
                        items(state.payments) { payment ->
                            state.currentAccount?.owner?.let {
                                PaymentItem(payment = payment,
                                    currentOwnerName = "${it.firstName} ${it.lastName}",
                                    onClick = onPaymentClick,
                                    currencyCode = state.currentAccount.currencyCode)
                            }
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 5.dp).alpha(0.3f),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        item {
                            AccountDetails(state = state, detailsVisible = detailsVisible)
                        }

                    }


                }
            }
        })
}

@Composable
fun CardsHeader(
    state: AccountContract.CardsUiState,
    onClose: () -> Unit,
    detailsVisible: Boolean,
    onToggleVisibility: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Account Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            val accountText = if (detailsVisible) state.accountNumber else maskString(state.accountNumber)
            Text(
                text = accountText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        VisibilityToggleIcon(
            isVisible = detailsVisible,
            onToggle = onToggleVisibility
        )
    }
}

@Composable
fun VisibilityToggleIcon(
    isVisible: Boolean,
    onToggle: () -> Unit
) {
    Icon(
        imageVector = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
        contentDescription = if (isVisible) "Hide Details" else "Show Details",
        modifier = Modifier
            .clickable { onToggle() }
            .padding(8.dp),
        tint = MaterialTheme.colorScheme.onSurface
    )
}



@Composable
fun CreditCardItem(
    cardNumber: String,
    cardHolder: String,
    expirationDate: String,
    onCardClick: (cardNumber: String) -> Unit,
    detailsVisible: Boolean,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(220.dp)
            .scale(0.9f)
            .clickable { onCardClick(cardNumber) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)

    ) {
        val formattedCardNumber = if (detailsVisible) {
            cardNumber.chunked(4).joinToString("  ")
        } else {
            cardNumber.chunked(4).joinToString("  ") { maskString(it) }
        }

        val displayedExpiration = if (detailsVisible) expirationDate else maskString(expirationDate)
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(16.dp).fillMaxSize()
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        modifier = Modifier
                            .width(40.dp)
                            .height(40.dp),
                        tint = MaterialTheme.colorScheme.tertiaryContainer
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    //Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Banka-3",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Text(
                    text = formattedCardNumber,
                    modifier = Modifier
                        .padding(top = 16.dp).fillMaxWidth(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "Card Holder",
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = cardHolder,
                            color = MaterialTheme.colorScheme.surface,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 16.sp,
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "Valid Until",
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = displayedExpiration,
                            color = MaterialTheme.colorScheme.surface,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
    }
}


@Composable
fun AccountDetails(
    state: AccountContract.CardsUiState,
    detailsVisible: Boolean
) {
    val account = state.accounts.find { it.accountNumber == state.accountNumber }
    if (account != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
               // .padding(16.dp),
                    ,
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Account Details",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(
                    label = "Account Number",
                    value = if (detailsVisible) account.accountNumber else maskString(account.accountNumber)
                )
                DetailRow(
                    label = "Balance",
                    value = if (detailsVisible) account.balance.toString() else maskString(account.balance.toString())
                )
                DetailRow(
                    label = "Available",
                    value = if (detailsVisible) account.availableBalance.toString() else maskString(account.availableBalance.toString())
                )
                DetailRow(
                    label = "Daily Limit",
                    value = if (detailsVisible) account.dailyLimit.toString() else maskString(account.dailyLimit.toString())
                )
                DetailRow(
                    label = "Monthly Limit",
                    value = if (detailsVisible) account.monthlyLimit.toString() else maskString(account.monthlyLimit.toString())
                )
                DetailRow(label = "Currency", value = account.currencyCode)
                account.creationDate?.let { DetailRow(label = "Created", value = it) }
                account.expirationDate?.let { DetailRow(label = "Expires", value = it) }
                DetailRow(label = "Status", value = account.status.name)
                DetailRow(label = "Ownership", value = account.ownershipType)
                DetailRow(label = "Category", value = account.accountCategory)
                DetailRow(
                    label = "Owner",
                    value = "${account.owner.firstName} ${account.owner.lastName}"
                )
            }
        }
    } else {
        Text(
            text = "No account details available.",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
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

fun maskString(input: String): String = "*".repeat(input.length)


@Composable
fun PaymentItem(
    payment: PaymentGetResponse,
    currentOwnerName: String,
    onClick: (String) -> Unit,
    currencyCode: String
) {
    val isOutgoing = payment.senderName == currentOwnerName
    val amountText = if (isOutgoing) "-${payment.amount} $currencyCode" else "+${payment.amount} $currencyCode"
    val amountColor = if (isOutgoing) MaterialTheme.colorScheme.error else Color.Green

    val formattedDate = formatDate(payment.date)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(payment.id.toString()) }
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = "Payment Icon",
                    modifier = Modifier.height(45.dp).width(45.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Payment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
            Text(
                text = amountText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}


fun formatDate(dateStr: String): String {
    return try {
        val parsedDate = LocalDateTime.parse(dateStr)
        parsedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    } catch (e: Exception) {
        dateStr
    }
}
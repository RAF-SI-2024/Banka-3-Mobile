package com.example.banka_3_mobile.bank.cards.details

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.banka_3_mobile.R
import com.example.banka_3_mobile.bank.cards.AccountContract
import com.example.banka_3_mobile.bank.cards.VisibilityToggleIcon
import com.example.banka_3_mobile.bank.cards.maskString
import com.example.banka_3_mobile.bank.cards.model.CardsGetResponse
import com.example.banka_3_mobile.bank.payments.model.PaymentGetResponse


fun NavGraphBuilder.cardDetailsPage(
    route: String,
    navController: NavController,
    arguments: List<NamedNavArgument>
) = composable(
    route = route,
    arguments = arguments
) {navBackStackEntry ->

    val cardDetailsViewModel = hiltViewModel<CardDetailsViewModel>(navBackStackEntry)
    val state by cardDetailsViewModel.state.collectAsState()

    CardDetailsScreen(
        state = state,
        onClose = {
            navController.navigateUp()
        }
    )
}

@Composable
fun CardDetailsScreen(
    state: CardDetailsContract.CardDetailsUiState,
    onClose: () -> Unit,
) {
    var detailsVisible by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxSize().padding(bottom = 60.dp),
        content = {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp).padding(top = 20.dp)
    ) {
        item {
            CardDetailsHeader(
                onClose = onClose,
                onToggleVisibility = { detailsVisible = !detailsVisible },
                detailsVisible = detailsVisible
            )
        }
        item {
            if (state.card != null) {
                FlippableCreditCard(
                    cardNumber = state.card.cardNumber,
                    cardHolder = "${state.card.owner.firstName} ${state.card.owner.lastName}",
                    expirationDate = state.card.expirationDate,
                    cvv = state.card.cvv,
                    detailsVisible = detailsVisible
                )
            } else {
                Text(
                    text = "Loading card details...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            if (state.card != null) {
                CardFullDetails(card = state.card!!, detailsVisible = detailsVisible)
            } else {
                Text("Loading card details...", style = MaterialTheme.typography.bodyLarge)
            }
        }

        }
    })
}

@Composable
fun FlippableCreditCard(
    cardNumber: String,
    cardHolder: String,
    expirationDate: String,
    cvv: String,
    detailsVisible: Boolean
) {
    var rotated by remember { mutableStateOf(false) }

    val formattedCardNumber = if (detailsVisible) {
        cardNumber.chunked(4).joinToString("  ")
    } else {
        cardNumber.chunked(4).joinToString("  ") { maskString(it) }
    }

    val displayedExpiration = if (detailsVisible) expirationDate else maskString(expirationDate)
    val displayedCvv = if (detailsVisible) cvv else maskString(cvv)

    val rotation by animateFloatAsState(
        targetValue = if (rotated) 180f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    val animateFront by animateFloatAsState(
        targetValue = if (!rotated) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    val animateBack by animateFloatAsState(
        targetValue = if (rotated) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(220.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
            .clickable { rotated = !rotated },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)

    ) {

        if (!rotated) {
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
                            .height(40.dp)
                            .graphicsLayer { alpha = animateFront },
                        tint = MaterialTheme.colorScheme.tertiaryContainer
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Banka-3",
                        modifier = Modifier
                            .graphicsLayer { alpha = animateFront },
                       style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Text(
                    text = formattedCardNumber,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .graphicsLayer { alpha = animateFront }.fillMaxWidth(),
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
                            modifier = Modifier.graphicsLayer { alpha = animateFront }
                        )
                        Text(
                            text = cardHolder,
                            color = MaterialTheme.colorScheme.surface,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 16.sp,
                            modifier = Modifier.graphicsLayer { alpha = animateFront }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "Valid Until",
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.graphicsLayer { alpha = animateFront }
                        )
                        Text(
                            text = displayedExpiration,
                            color = MaterialTheme.colorScheme.surface,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 16.sp,
                            modifier = Modifier.graphicsLayer { alpha = animateFront }
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.padding(top = 20.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier.graphicsLayer { alpha = animateBack },
                    thickness = 50.dp,
                    color = Color.Black
                )

                Text(
                    text = displayedCvv,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(10.dp)
                        .background(Color.White)
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = animateBack
                            rotationY = rotation
                        }
                        .padding(10.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.End
                )

                /**
                 * Credits to Mehmet Yozgatli for Animation
                 */
            }
        }
    }
}


@Composable
fun CardDetailsHeader(
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

            Text(
                text = "Card Details",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )


        VisibilityToggleIcon(
            isVisible = detailsVisible,
            onToggle = onToggleVisibility
        )
    }
}

@Composable
fun CardFullDetails(
    card: CardsGetResponse,
    detailsVisible: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DetailRow(
            label = "Card Number",
            value = if (detailsVisible) card.cardNumber else maskString(card.cardNumber)
        )
        DetailRow(
            label = "CVV",
            value = if (detailsVisible) card.cvv else maskString(card.cvv)
        )
        DetailRow(
            label = "Creation Date",
            value = if (detailsVisible) card.creationDate else maskString(card.creationDate)
        )
        DetailRow(
            label = "Expiration Date",
            value = if (detailsVisible) card.expirationDate else maskString(card.expirationDate)
        )
        DetailRow(
            label = "Card Limit",
            value = if (detailsVisible) card.cardLimit.toString() else maskString(card.cardLimit.toString())
        )
        DetailRow(
            label = "Account Number",
            value = if (detailsVisible) card.accountNumber else maskString(card.accountNumber)
        )
        card.type?.let {
            DetailRow(label = "Type", value = it)
        }
        card.name?.let {
            DetailRow(label = "Name", value = it)
        }
        DetailRow(label = "Status", value = card.status.name)
        DetailRow(label = "Owner", value = "${card.owner.firstName} ${card.owner.lastName}")
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
}

fun maskString(input: String): String = "*".repeat(input.length)

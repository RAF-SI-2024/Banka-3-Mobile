package com.example.banka_3_mobile.verification

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.banka_3_mobile.navigation.AppNavigationViewModel
import com.example.banka_3_mobile.verification.mapper.mapChangeLimitDetails
import com.example.banka_3_mobile.verification.mapper.mapNewCreditCardVerification
import com.example.banka_3_mobile.verification.mapper.mapPaymentOrTransferDetails
import com.example.banka_3_mobile.verification.model.VerificationRequest
import com.example.banka_3_mobile.verification.model.VerificationStatus
import com.example.banka_3_mobile.verification.model.VerificationType
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun NavGraphBuilder.verificationPage(
    route: String,
    navController: NavController,
) = composable(
    route = route,
) {navBackStackEntry ->

    val verificationViewModel = hiltViewModel<VerificationViewModel>(navBackStackEntry)

    val state by verificationViewModel.state.collectAsState()


    VerificationScreen(
        state = state,
        eventPublisher = {
            verificationViewModel.setEvent(it)
        },
        onClose = {
            navController.navigateUp()
        },
    )
}

@Composable
fun VerificationScreen(
    state: VerificationContract.VerificationUiState,
    eventPublisher: (uiEvent: VerificationContract.VerificationUIEvent) -> Unit,
    onClose: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        content = {

            when {
                state.fetching ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(modifier = Modifier.align(Alignment.Center), text = "Loading payments...")
                    }
                state.error != null ->
                    Text(color = MaterialTheme.colorScheme.error, text = "${state.error}")
                else -> {
                    VerifyColumn(state = state, eventPublisher = eventPublisher, onClose = onClose)
                }
            }
        })
}

@Composable
fun VerifyColumn(
    state: VerificationContract.VerificationUiState,
    eventPublisher: (uiEvent: VerificationContract.VerificationUIEvent) -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NavHeader(title = "Requests")
        VerificationsColumn(
            state = state,
            eventPublisher = eventPublisher,
        )
    }
}

@Composable
fun NavHeader(
    title: String
)  {
    Row(modifier = Modifier
        .fillMaxWidth().padding(vertical = 8.dp)
        ){
        Text(text = title, style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationsColumn (
    state: VerificationContract.VerificationUiState,
    eventPublisher: (uiEvent: VerificationContract.VerificationUIEvent) -> Unit,
){
    val scrollState = rememberLazyListState()
    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { eventPublisher(VerificationContract.VerificationUIEvent.PullToRefreshTrigger) },
    ) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize().padding(bottom = 80.dp)
            ,
            verticalArrangement = Arrangement.Top,
        ) {
            if (state.activeRequests.isNotEmpty()) {
                item {
                    Text(style = MaterialTheme.typography.headlineSmall, text = "Pending Requests")
                }
                items(state.activeRequests) { request ->
                    Column {
                        ActiveVerificationItem(
                            data = request,
                            eventPublisher = eventPublisher,
                        )
                          Spacer(modifier = Modifier.height(10.dp))
                    }

                }
            } else {
                item {
                    Text(style = MaterialTheme.typography.bodyLarge, text = "You have no pending requests.")
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                if (state.requestHistory.isNotEmpty()) {
                    Text(style = MaterialTheme.typography.headlineSmall, text = "Request History")
                } else {
                    Text(style = MaterialTheme.typography.titleSmall, text = "No previous verification requests found.")
                }

            }
            items(state.requestHistory) { request ->
                Column {
                    InactiveVerificationItem(
                        data = request,
                    )
                     Spacer(modifier = Modifier.height(10.dp))
                }

            }
        }
    }

}

@Composable
fun ActiveVerificationItem(
    data: VerificationRequest,
    eventPublisher: (uiEvent: VerificationContract.VerificationUIEvent) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                VerificationLeftContent(data)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = {
                    eventPublisher(VerificationContract.VerificationUIEvent.VerifyPending(data.id))
                }) {
                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .alpha(0.3f),
                        imageVector = Icons.Sharp.Check,
                        contentDescription = "Accept",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = {
                    eventPublisher(VerificationContract.VerificationUIEvent.DeclinePending(data.id))
                }) {
                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .alpha(0.3f),
                        imageVector = Icons.Sharp.Close,
                        contentDescription = "Decline",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
@Composable
fun InactiveVerificationItem(
    data: VerificationRequest
) {
    val formattedDateTime = try {
        val ldt = LocalDateTime.parse(data.createdAt)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        ldt.format(formatter)
    } catch (e: Exception) {
        data.createdAt
    }

    val expired = if (data.status == VerificationStatus.PENDING) {
        try {
            val createdTime = LocalDateTime.parse(data.createdAt)
            Duration.between(createdTime, LocalDateTime.now()).toMinutes() >= 5
        } catch (e: Exception) {
            false
        }
    } else {
        false
    }

    val displayStatus = if (expired) "EXPIRED" else data.status.name
    val statusColor =  when (data.status) {
        VerificationStatus.APPROVED -> Color.Green
        VerificationStatus.DENIED -> MaterialTheme.colorScheme.error
        VerificationStatus.PENDING -> MaterialTheme.colorScheme.secondary
        VerificationStatus.EXPIRED -> MaterialTheme.colorScheme.tertiary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                VerificationLeftContent(data)
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = displayStatus,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = statusColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedDateTime,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}


@Composable
fun VerificationLeftContent(data: VerificationRequest) {
    when (data.verificationType) {
        VerificationType.PAYMENT -> {
            Text(
                text = "Payment",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            val details = mapPaymentOrTransferDetails(data.details)
            details?.let {
                Text(
                    text = it.fromAccountNumber.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "-${it.amount}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        VerificationType.TRANSFER -> {
            Text(
                text = "Transfer",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            val details = mapPaymentOrTransferDetails(data.details)
            details?.let {
                Text(
                    text = "${it.fromAccountNumber}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "↓",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "${it.toAccountNumber}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = it.amount.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        VerificationType.CHANGE_LIMIT -> {
            Text(
                text = "Change Limit",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            val changeDetails = mapChangeLimitDetails(data.details)
            Text(
                text = changeDetails.accountNumber,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp
            )
            Text(
                text = "${changeDetails.oldLimit} -> ${changeDetails.newLimit}",
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
        VerificationType.CARD_REQUEST -> {
            Text(
                text = "New Card",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            val cardDetails = mapNewCreditCardVerification(data.details)
            Text(
                text = cardDetails.name,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp
            )
            Text(
                text = cardDetails.type,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = cardDetails.accountNumber,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp
            )
            Text(
                text = cardDetails.issuer,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        else -> {
            Text(
                text = data.verificationType.name,
                fontWeight = FontWeight.Bold
            )
        }
    }
}




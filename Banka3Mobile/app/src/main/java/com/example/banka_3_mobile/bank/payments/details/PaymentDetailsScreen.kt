package com.example.banka_3_mobile.bank.payments.details

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.banka_3_mobile.bank.payments.details.model.PaymentDetailsGetResponse


fun NavGraphBuilder.paymentDetailsPage(
    route: String,
    navController: NavController,
) = composable(
    route = route,
) {navBackStackEntry ->


    val paymentDetailsViewModel = hiltViewModel<PaymentDetailsViewModel>(navBackStackEntry)
    val state by paymentDetailsViewModel.state.collectAsState()
    PaymentDetailsScreen(
        state = state,
        onClose = { navController.navigateUp() },
    )
}

@Composable
fun PaymentDetailsScreen(
    state: PaymentDetailsContract.PaymentDetailsUiState,
    onClose: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp).padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )  {
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
                        text = "Payment Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            item {
                if (state.fetching) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (state.error != null) {
                    Text(
                        text = "Error: ${state.error}",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else if (state.payment != null) {
                    PaymentDetailsCard(payment = state.payment)
                } else {
                    Text("No payment details available.")
                }
            }
        }
    }
}

@Composable
fun PaymentDetailsCard(payment: PaymentDetailsGetResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            PaymentDetailRow(label = "Payment ID", value = payment.id.toString())
            payment.senderName?.let { PaymentDetailRow(label = "Sender Name", value = it) }
            PaymentDetailRow(label = "Amount", value = payment.amount.toString())
            PaymentDetailRow(label = "Receiver Account", value = payment.accountNumberReceiver)
            payment.paymentCode?.let { PaymentDetailRow(label = "Payment Code", value = it) }
            payment.purposeOfPayment?.let { PaymentDetailRow(label = "Purpose", value = it) }
            PaymentDetailRow(label = "Reference", value = payment.referenceNumber ?: "N/A")
            PaymentDetailRow(label = "Date", value = payment.date)
            PaymentDetailRow(label = "Status", value = payment.status.toString())
            PaymentDetailRow(label = "Card Number", value = payment.cardNumber ?: "N/A")
        }
    }
}

@Composable
fun PaymentDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}

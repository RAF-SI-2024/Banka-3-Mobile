package com.example.banka_3_mobile.bank.payments.ips.create

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.banka_3_mobile.bank.payments.ips.generateQRBitmap
import com.example.banka_3_mobile.bank.payments.transfer.AccountSelector

fun NavGraphBuilder.ipsCreate(
    route: String,
    navController: NavController,
) = composable(
    route = route,
) {navBackStackEntry ->

    val ipsCreateViewModel = hiltViewModel<IPSCreateViewModel>(navBackStackEntry)
    val state by ipsCreateViewModel.state.collectAsState()
    IPSCreateScreen(
        state = state,
        eventPublisher = {
            ipsCreateViewModel.setEvent(it)
        },
        onClose = {
            navController.navigateUp()
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun IPSCreateScreen(
    state: IPSCreateContract.IpsCreateUiState,
    eventPublisher: (uiEvent: IPSCreateContract.IpsCreateUiEvent) -> Unit,
    onClose: () -> Unit
) {
    Scaffold (
        modifier = Modifier.fillMaxSize().padding(bottom = 70.dp),
        topBar = {
           CenterAlignedTopAppBar(
               title = {
                   Text(
                       text = "IPS Qr Code Creator",
                       style = MaterialTheme.typography.headlineMedium,
                       fontWeight = FontWeight.ExtraBold
                   )
               },
               navigationIcon = {
                   IconButton(onClick = onClose) {
                       Icon(
                           imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                           contentDescription = "Back"
                       )
                   }
               }
           )
        }
        ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),//.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!state.generatedQrCode) {
                IPSFormScreen(
                    state = state,
                    eventPublisher = eventPublisher,
                )
            } else {
                QRCodeShowcaseScreen(state = state)
            }


        }
    }
}

@Composable
fun IPSFormScreen(
    state: IPSCreateContract.IpsCreateUiState,
    eventPublisher: (uiEvent: IPSCreateContract.IpsCreateUiEvent) -> Unit,
) {
    if (state.accounts.isNotEmpty()) {
        AccountSelector(
            label = "Account the payment will be sent to",
            accounts = state.accounts,
            selectedAccountNumber = state.toAccount,
            onAccountSelected = { accountNumber ->
                eventPublisher(IPSCreateContract.IpsCreateUiEvent.ToAccountSelected(accountNumber))
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
    OutlinedTextField(
        value = state.amount?.takeIf { it > 0 }?.toString() ?: "",
        onValueChange = { input ->
            val parsed = input.toFloatOrNull()
            eventPublisher(
                IPSCreateContract.IpsCreateUiEvent.TypingAmount(
                    parsed
                )
            )
        },

        label = { Text("Amount (optional)") },
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
        onValueChange = { eventPublisher(IPSCreateContract.IpsCreateUiEvent.TypingPaymentCode(it)) },
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
        onValueChange = { eventPublisher(IPSCreateContract.IpsCreateUiEvent.TypingPaymentPurpose(it)) },
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
        onValueChange = { eventPublisher(IPSCreateContract.IpsCreateUiEvent.TypingReferenceNumber(it)) },
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
        onClick = { eventPublisher(IPSCreateContract.IpsCreateUiEvent.CreateIPSQRCode) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
    ) {
        Text(text = "Create IPS QR Code", fontSize = 18.sp)
    }
}

@Composable
fun QRCodeShowcaseScreen(
    state: IPSCreateContract.IpsCreateUiState
) {
    val qrText = state.qrText ?: return
    val bitmap = remember(qrText) { generateQRBitmap(qrText) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        IPSPaymentItem("To Account", state.toAccount)
        IPSPaymentItem("Recipient", state.accountName)
        state.amount?.let {
            IPSPaymentItem("Amount", "$it ${state.selectedAccountCurrencyCode}")
        }
        IPSPaymentItem("Payment Code", state.paymentCode)
        IPSPaymentItem("Purpose", state.purpose)
        state.referenceNumber?.takeIf { it.isNotBlank() }?.let {
            IPSPaymentItem("Reference", it)
        }
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Generated QR Code",
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        DownloadQRCodeButton(bitmap)
    }
}

@Composable
fun DownloadQRCodeButton(bitmap: Bitmap) {
    val context = LocalContext.current

    Button(
        onClick = {
            val filename = "ips_qr_code_${System.currentTimeMillis()}.png"
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it).use { outputStream ->
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(
            "Download QR Code",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}


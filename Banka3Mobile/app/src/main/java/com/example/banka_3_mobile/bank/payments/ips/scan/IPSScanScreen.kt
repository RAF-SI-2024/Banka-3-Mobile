package com.example.banka_3_mobile.bank.payments.ips.scan

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.banka_3_mobile.bank.payments.ips.create.IPSPaymentItem
import com.example.banka_3_mobile.bank.payments.new_payment.FromAccountSelector
import com.example.banka_3_mobile.bank.payments.new_payment.NewPaymentContract
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage


fun NavGraphBuilder.ipsScan(
    route: String,
    navController: NavController,
) = composable(route = route) {
    val viewModel = hiltViewModel<IPSScanViewModel>()
    val state by viewModel.state.collectAsState()
    IPSScanScreen(
        state = state,
        eventPublisher = {
            viewModel.setEvent(it)
        },
        onClose = { navController.navigateUp() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IPSScanScreen(
    state: IPSScanContract.IPSScanUiState,
    eventPublisher: (uiEvent: IPSScanContract.IPSScanUiEvent) -> Unit,
    onClose: () -> Unit,
) {


    Scaffold(
        modifier = Modifier.padding(bottom = 70.dp),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("IPS Payment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!state.scanCompleted) {
                ScanScreen(
                    state = state,
                    eventPublisher = eventPublisher,
                )
            }
            else {
                IPSPaymentScreen(
                    state = state,
                    eventPublisher = eventPublisher,
                    onClose = onClose,
                )
            }

        }
    }
}

@Composable
fun ScanScreen(
    state: IPSScanContract.IPSScanUiState,
    eventPublisher: (uiEvent: IPSScanContract.IPSScanUiEvent) -> Unit,
) {
    val context = LocalContext.current

    val cameraPermission = Manifest.permission.CAMERA
    val hasPermission = remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, cameraPermission) == PackageManager.PERMISSION_GRANTED)
    }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission.value = granted
    }

    Text("Scan IPS QR Code with your camera",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        )

    Spacer(modifier = Modifier.height(10.dp))

    if (hasPermission.value) {
        Box(
            modifier = Modifier
                //.aspectRatio(1f)
                .height((screenHeight.value/1.7).dp)
                .background(Color.Black, shape = RoundedCornerShape(12.dp))
        ) {
            CameraPreview(onQRCodeScanned = { qrContent ->
                eventPublisher(IPSScanContract.IPSScanUiEvent.QRCodeScanned(qrContent))
            },
            )
        }
    } else {
        Text("Camera permission is required to scan QR codes.")
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { launcher.launch(cameraPermission) }) {
            Text("Grant Camera Permission")
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    state.scannedQRCode?.let {
        Text("Scanned Text:", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(it, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun IPSPaymentScreen(
    state: IPSScanContract.IPSScanUiState,
    eventPublisher: (uiEvent: IPSScanContract.IPSScanUiEvent) -> Unit,
    onClose: () -> Unit,
) {
    val ips = state.scannedIPS ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Scanned IPS Details", style = MaterialTheme.typography.titleLarge)

        ips.accountNumber?.let { IPSPaymentItem("Account", it) }
        ips.recipientName?.let { IPSPaymentItem("Recipient", it) }
        ips.amount?.let {
            IPSPaymentItem("Amount", it.toString())
        }
        ips.paymentCode?.let { IPSPaymentItem("Payment Code", it) }
        ips.purpose?.let { IPSPaymentItem("Purpose", it) }
        ips.referenceNumber?.takeIf { it.isNotBlank() }?.let {
            IPSPaymentItem("Reference Number", it)
        }


        if (ips.amount == null) {
            OutlinedTextField(
                value = if (state.amount == 0) "" else state.amount.toString(),
                onValueChange = { input ->
                    eventPublisher(
                        IPSScanContract.IPSScanUiEvent.TypingAmount(
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
        }

        if (state.accounts.isNotEmpty()) {
            FromAccountSelector(
                accounts = state.accounts,
                selectedAccount = state.fromAccount,
                onAccountSelected = { accountNumber ->
                    eventPublisher(IPSScanContract.IPSScanUiEvent.FromAccountSelected(accountNumber))
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { eventPublisher(IPSScanContract.IPSScanUiEvent.SendPayment) },
            //enabled = !state.sending,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Payment")
        }

        if (state.paymentSuccess) {
            Text("Payment sent successfully!", color = Color.Green, fontWeight = FontWeight.Bold)
        }

        state.error?.let {
            Text("Error: $it", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }


    if (state.paymentSuccess) {
        AlertDialog(
            onDismissRequest = { eventPublisher(IPSScanContract.IPSScanUiEvent.CloseDialogAndNavigate) },
            title = { Text("Payment sent successfully!") },
            confirmButton = {
                Button(onClick = {
                    eventPublisher(IPSScanContract.IPSScanUiEvent.CloseDialogAndNavigate)
                    onClose()
                }) {
                    Text("Close")
                }
            }
        )
    }

}


@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    onQRCodeScanned: (String) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),

        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val qrScanner = BarcodeScanning.getClient()

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                qrScanner.process(inputImage)
                                    .addOnSuccessListener { barcodes ->
                                        for (barcode in barcodes) {
                                            barcode.rawValue?.let { value ->
                                                onQRCodeScanned(value)
                                            }
                                        }
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            } else {
                                imageProxy.close()
                            }
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    analysis
                )
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}

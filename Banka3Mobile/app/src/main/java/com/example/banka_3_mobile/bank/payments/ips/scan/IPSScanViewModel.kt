package com.example.banka_3_mobile.bank.payments.ips.scan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.bank.payments.ips.isValidIPSQR
import com.example.banka_3_mobile.bank.payments.ips.model.IPSModel
import com.example.banka_3_mobile.bank.payments.ips.parseIPSQR
import com.example.banka_3_mobile.bank.payments.ips.parseIPSQRRaw
import com.example.banka_3_mobile.bank.payments.new_payment.model.NewPaymentPostRequest
import com.example.banka_3_mobile.bank.repository.BankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class IPSScanViewModel @Inject constructor(
    private val bankRepository: BankRepository,
): ViewModel() {
    private val _state = MutableStateFlow(IPSScanContract.IPSScanUiState())
    val state: StateFlow<IPSScanContract.IPSScanUiState> = _state.asStateFlow()

    private fun setState(reducer: IPSScanContract.IPSScanUiState.() -> IPSScanContract.IPSScanUiState) =
        _state.update(reducer)

    private  val events = MutableSharedFlow<IPSScanContract.IPSScanUiEvent>()
    fun setEvent(event: IPSScanContract.IPSScanUiEvent) = viewModelScope.launch {
        events.emit(event)
    }

    init {
        fetchAccounts()
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect {
                when (it) {
                    is IPSScanContract.IPSScanUiEvent.QRCodeScanned -> {
                        /*try {
                            val model = parseIPSQR(it.content)
                            setState {
                                copy(
                                    scannedQRCode = it.content,
                                    scannedIPS = model,
                                    scanCompleted = true
                                )
                            }
                        } catch (e: Exception) {
                            setState {
                                copy(
                                    scannedQRCode = it.content,
                                    scanCompleted = false,
                                    scannedIPS = null
                                )
                            }
                        }*/
                        convertScanToIPS(it.content)
                    }


                    is IPSScanContract.IPSScanUiEvent.FromAccountSelected -> {
                        setState { copy(fromAccount = it.fromAccount) }
                    }

                    is IPSScanContract.IPSScanUiEvent.TypingAmount -> {
                        setState { copy(amount = it.amount.toInt()) }
                        if (it.amount <= 0f) {
                            setState {
                                copy(amountError = true, amountErrorText = "Amount can't be zero or negative.")
                            }
                        } else {
                            setState { copy(amountError = false) }
                        }
                    }

                    IPSScanContract.IPSScanUiEvent.SendPayment -> {
                        if (isValidForm()) sendScannedPayment()
                    }

                    IPSScanContract.IPSScanUiEvent.CloseDialogAndNavigate ->
                        setState { copy(paymentSuccess = false) }
                }
            }
        }
    }

    private fun isValidForm(): Boolean {
        val model = state.value.scannedIPS
        val parsedAmount = model?.amount
            ?.replace(",", ".")
            ?.replace(Regex("[^0-9.]"), "")
            ?.toFloatOrNull()

        val hasAmountFromQR = parsedAmount != null && parsedAmount > 0
        val isManualAmountValid = state.value.amount > 0

        val valid = hasAmountFromQR || isManualAmountValid

        setState {
            copy(
                amountError = !valid,
                amountErrorText = if (!valid) "Amount is required and must be greater than 0." else "",
                invalidForm = !valid
            )
        }

        return valid
    }

    private fun fetchAccounts() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val accountResponse = bankRepository.getAccounts()
                    setState { copy(accounts = accountResponse,
                        fromAccount = accountResponse.firstOrNull()?.accountNumber ?: "",
                    ) }
                }
                /*setState{
                    copy(scannedIPS = IPSModel(
                        accountNumber = "222222222222222222",
                        recipientName = "Jovan Jovanovic",
                        amount = "EUR11,00",
                        paymentCode = "123",
                        purpose = "Nesto lepo",
                        referenceNumber = "1"
                    )
                    )
                }*/

            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error loading user info: ${e.message}")
            }
        }
    }

    private fun convertScanToIPS(qrContent: String) {
            val rawMap = parseIPSQRRaw(qrContent)

            if (isValidIPSQR(rawMap)) {
                val model = IPSModel(
                    accountNumber = rawMap["R"],
                    recipientName = rawMap["N"]?.replace("\\r\\n", "\n"),
                    amount = rawMap["I"],
                    payer = rawMap["P"]?.replace("\\r\\n", "\n"),
                    paymentCode = rawMap["SF"],
                    purpose = rawMap["S"],
                    referenceNumber = rawMap["RO"]
                )

                setState {
                    copy(
                        scannedQRCode = qrContent,
                        scannedIPS = model,
                        scanCompleted = true
                    )
                }
            } else {
                setState {
                    copy(
                        scannedQRCode = qrContent,
                        scannedIPS = null,
                        scanCompleted = false
                    )
                }
            }

    }
    private fun sendScannedPayment() {
        Log.d("raf", "SEND PAYMENT U IPS")
        viewModelScope.launch {
            try {
                val model = state.value.scannedIPS

                val amountFloat: Float? = model?.amount
                    ?.replace(",", ".")
                    ?.replace(Regex("[^0-9.]"), "")
                    ?.toFloatOrNull()
                    ?: state.value.amount.takeIf { it > 0 }?.toFloat()

                if (model?.accountNumber!=null && model.purpose !=null
                    && amountFloat !=null && model.paymentCode!=null) {
                    if (model.referenceNumber==null) {
                        val response = withContext(Dispatchers.IO) {
                            bankRepository.sendPayment(
                                NewPaymentPostRequest(
                                    senderAccountNumber = state.value.fromAccount,
                                    receiverAccountNumber = model.accountNumber,
                                    amount = amountFloat,
                                    paymentCode = model.paymentCode,
                                    purposeOfPayment = model.purpose,
                                )
                            )
                        }
                        if (response) {
                            setState { copy(paymentSuccess = true, error = null) }
                        } else {
                            setState { copy(error = "Payment failed. Check data.") }
                        }
                    } else {
                        val response = withContext(Dispatchers.IO) {
                            bankRepository.sendPayment(
                                NewPaymentPostRequest(
                                    senderAccountNumber = state.value.fromAccount,
                                    receiverAccountNumber = model.accountNumber,
                                    amount = amountFloat,
                                    paymentCode = model.paymentCode,
                                    purposeOfPayment = model.purpose,
                                    referenceNumber = model.referenceNumber
                                )
                            )
                        }
                        if (response) {
                            setState { copy(paymentSuccess = true, error = null) }
                        } else {
                            setState { copy(error = "Payment failed. Check data.") }
                        }
                    }

                }




            } catch (e: Exception) {
                setState { copy(error = e.message) }
            }
        }
    }


}
package com.example.banka_3_mobile.bank.payments.ips.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.bank.payments.ips.generateIPSQRString
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
class IPSCreateViewModel @Inject constructor(
    private val bankRepository: BankRepository,
): ViewModel(){
    private val _state = MutableStateFlow(IPSCreateContract.IpsCreateUiState())
    val state: StateFlow<IPSCreateContract.IpsCreateUiState> = _state.asStateFlow()

    private fun setState(reducer: IPSCreateContract.IpsCreateUiState.() -> IPSCreateContract.IpsCreateUiState) =
        _state.update(reducer)

    private  val events = MutableSharedFlow<IPSCreateContract.IpsCreateUiEvent>()
    fun setEvent(event: IPSCreateContract.IpsCreateUiEvent) = viewModelScope.launch {
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
                    IPSCreateContract.IpsCreateUiEvent.CreateIPSQRCode -> {
                        if (isValidForm())
                            createIPSQRCode()
                    }
                    is IPSCreateContract.IpsCreateUiEvent.ToAccountSelected -> {
                        val accountNumber = it.toAccount
                        val selectedAccount = state.value.accounts.firstOrNull { acc -> acc.accountNumber == accountNumber }
                        val name = selectedAccount?.owner?.let { owner -> "${owner.firstName} ${owner.lastName}" } ?: ""
                        val currency = selectedAccount?.currencyCode ?: "RSD"
                        setState {
                            copy(
                                toAccount = accountNumber,
                                accountName = name,
                                selectedAccountCurrencyCode = currency
                            )
                        }
                    }
                    is IPSCreateContract.IpsCreateUiEvent.TypingAmount -> {
                        setState { copy(amount = it.amount) }

                        if (it.amount != null && it.amount <= 0f) {
                            setState {
                                copy(
                                    amountError = true,
                                    amountErrorText = "Amount must be greater than 0."
                                )
                            }
                        } else {
                            setState { copy(amountError = false) }
                        }
                    }

                    is IPSCreateContract.IpsCreateUiEvent.TypingPaymentCode -> {
                        setState { copy(paymentCode = it.paymentCode) }
                        if (state.value.paymentCode=="") {
                            setState {
                                copy(paymentCodeError = true,
                                    paymentCodeErrorText = "Payment code cannot be empty.")
                            }
                        } else {
                            setState { copy(paymentCodeError = false) }
                        }
                    }
                    is IPSCreateContract.IpsCreateUiEvent.TypingPaymentPurpose ->
                    {
                        setState { copy(purpose = it.paymentPurpose) }
                        if (state.value.purpose == "") {
                            setState {
                                copy(purposeError = true,
                                    purposeErrorText = "Purpose cannot be empty.")
                            }
                        } else {
                            setState { copy(purposeError = false) }
                        }
                    }
                    is IPSCreateContract.IpsCreateUiEvent.TypingReferenceNumber ->
                        setState {
                            copy(referenceNumber = it.referenceNumber)
                        }
                }
            }
        }
    }

    private fun isValidForm(): Boolean {
        var valid = true

        if (state.value.amount != null && state.value.amount!! <= 0f) {
            setState {
                copy(
                    amountError = true,
                    amountErrorText = "Amount must be greater than 0.",
                    invalidForm = true
                )
            }
            valid = false
        }

        if (state.value.purpose.isBlank()) {
            setState {
                copy(
                    purposeError = true,
                    purposeErrorText = "Purpose cannot be empty.",
                    invalidForm = true
                )
            }
            valid = false
        }

        if (state.value.paymentCode.isBlank()) {
            setState {
                copy(
                    paymentCodeError = true,
                    paymentCodeErrorText = "Payment code cannot be empty.",
                    invalidForm = true
                )
            }
            valid = false
        }

        setState { copy(invalidForm = !valid) }
        return valid
    }



    private fun fetchAccounts() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val accountResponse = bankRepository.getAccounts()
                    setState {
                        val firstAccount = accountResponse.firstOrNull()
                        copy(
                            accounts = accountResponse,
                            toAccount = firstAccount?.accountNumber ?: "",
                            accountName = "${firstAccount?.owner?.firstName ?: ""} ${firstAccount?.owner?.lastName ?: ""}",
                            selectedAccountCurrencyCode = firstAccount?.currencyCode ?: "RSD"
                        )
                    }
                }

            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error loading user info: ${e.message}")
            }
        }
    }

    private fun createIPSQRCode() {
        val stateNow = state.value
        val qrText = if (stateNow.amount != null && stateNow.amount > 0f) {
            generateIPSQRString(
                accountNumber = stateNow.toAccount,
                recipientName = stateNow.accountName,
                amount = stateNow.amount.toInt(),
                paymentCode = stateNow.paymentCode,
                purpose = stateNow.purpose,
                referenceNumber = stateNow.referenceNumber,
                currencyCode = stateNow.selectedAccountCurrencyCode,
            )
        } else {
            generateIPSQRString(
                accountNumber = stateNow.toAccount,
                recipientName = stateNow.accountName,
                amount = null,
                paymentCode = stateNow.paymentCode,
                purpose = stateNow.purpose,
                referenceNumber = stateNow.referenceNumber,
                currencyCode = stateNow.selectedAccountCurrencyCode,
            )
        }
        Log.d("QR", qrText)

        setState { copy(generatedQrCode = true, qrText = qrText) }
    }

}

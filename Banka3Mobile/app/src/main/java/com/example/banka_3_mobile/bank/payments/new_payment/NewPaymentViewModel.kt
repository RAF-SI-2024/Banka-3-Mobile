package com.example.banka_3_mobile.bank.payments.new_payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class NewPaymentViewModel @Inject constructor(
    private val bankRepository: BankRepository
): ViewModel()
{

    private val _state = MutableStateFlow(NewPaymentContract.NewPaymentUiState())
    val state: StateFlow<NewPaymentContract.NewPaymentUiState> = _state.asStateFlow()

    private fun setState(reducer: NewPaymentContract.NewPaymentUiState.() -> NewPaymentContract.NewPaymentUiState) =
        _state.update(reducer)

    private  val events = MutableSharedFlow<NewPaymentContract.NewPaymentUiEvent>()
    fun setEvent(event: NewPaymentContract.NewPaymentUiEvent) = viewModelScope.launch {
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
                    is NewPaymentContract.NewPaymentUiEvent.FromAccountSelected ->
                        setState { copy(fromAccount = it.fromAccount) }
                    NewPaymentContract.NewPaymentUiEvent.SendPayment ->
                        if (isValidForm())
                            validateAndSendPayment()
                    is NewPaymentContract.NewPaymentUiEvent.TypingAmount -> {
                        setState { copy(amount = it.amount.toInt()) }
                        if (state.value.amount == 0 || state.value.amount.toString()=="") {
                            setState {
                                copy(amountError = true,
                                    amountErrorText = "Amount can't be empty or zero.")
                            }
                        } else {
                            setState { copy(amountError = false) }
                        }
                    }

                    is NewPaymentContract.NewPaymentUiEvent.TypingPaymentCode -> {
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
                    is NewPaymentContract.NewPaymentUiEvent.TypingPaymentPurpose ->
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

                    is NewPaymentContract.NewPaymentUiEvent.TypingReferenceNumber ->
                        setState { copy(referenceNumber = it.referenceNumber) }
                    is NewPaymentContract.NewPaymentUiEvent.TypingToAccount -> {
                        setState { copy(toAccount = it.toAccountNumber) }
                        if (state.value.toAccount.length<8) {
                            setState {
                                copy(toAccountError = true,
                                    toAccountErrorText = "Account number cannot be shorter than 8 characters.")
                            }
                        } else {
                            setState { copy(toAccountError = false) }
                        }
                    }

                }
            }
        }
    }

    private fun isValidForm(): Boolean {
        if (state.value.amount == 0 || state.value.amount.toString()=="") {
            setState {
                copy(amountError = true,
                    amountErrorText = "Amount can't be empty or zero.",
                    invalidForm = true)
            }
        } else if (state.value.toAccount.length<8) {
            setState {
                copy(toAccountError = true,
                    toAccountErrorText = "Account number cannot be shorter than 8 characters.",
                    invalidForm = true)
            }
        } else if (state.value.purpose == "") {
            setState {
                copy(purposeError = true,
                    purposeErrorText = "Purpose cannot be empty.",
                    invalidForm = true)
            }
        } else if (state.value.paymentCode=="") {
            setState {
                copy(paymentCodeError = true,
                    paymentCodeErrorText = "Payment code cannot be empty.",
                    invalidForm = true)
            }
        }
        else {
            setState { copy(invalidForm = false) }
        }

        return !state.value.invalidForm
    }


    private fun fetchAccounts() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val accountResponse = bankRepository.getAccounts()
                    setState { copy(accounts = accountResponse,
                        fromAccount = accountResponse.firstOrNull()?.accountNumber ?: "") }
                }

            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error loading user info: ${e.message}")
            }
        }
    }

    private fun validateAndSendPayment() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    bankRepository.sendPayment(NewPaymentPostRequest(
                        senderAccountNumber = state.value.fromAccount,
                        receiverAccountNumber = state.value.toAccount,
                        amount = state.value.amount.toFloat(),
                        paymentCode = state.value.paymentCode,
                        purposeOfPayment = state.value.purpose,
                        referenceNumber = state.value.referenceNumber
                    ))
                }
                if (response)
                    setState { copy(paymentSuccess = true,
                        error = null) }
                else {
                    setState { copy(error = "Cannot find receiver account number.") }
                }
                Log.d("raf", "POST for payment response: $response")

            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error loading user info: ${e.message}")
            }
        }
    }
}
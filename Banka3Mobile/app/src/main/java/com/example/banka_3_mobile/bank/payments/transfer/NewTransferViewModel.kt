package com.example.banka_3_mobile.bank.payments.transfer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.bank.payments.transfer.model.NewTransferPostRequest
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
class NewTransferViewModel @Inject constructor(
    private val bankRepository: BankRepository
): ViewModel()
{
    private val _state = MutableStateFlow(NewTransferContract.NewTransferUiState())
    val state: StateFlow<NewTransferContract.NewTransferUiState> = _state.asStateFlow()

    private fun setState(reducer: NewTransferContract.NewTransferUiState.() -> NewTransferContract.NewTransferUiState) =
        _state.update(reducer)

    private  val events = MutableSharedFlow<NewTransferContract.NewTransferUiEvent>()
    fun setEvent(event: NewTransferContract.NewTransferUiEvent) = viewModelScope.launch {
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
                    is NewTransferContract.NewTransferUiEvent.FromAccountSelected ->
                        setState { copy(fromAccount = it.fromAccount) }
                    is NewTransferContract.NewTransferUiEvent.ToAccountSelected ->
                        setState { copy(toAccount = it.toAccountNumber) }
                    is NewTransferContract.NewTransferUiEvent.TypingAmount -> {
                        setState { copy(amount = it.amount.toInt()) }
                        isValidForm()
                    }
                    NewTransferContract.NewTransferUiEvent.SendTransaction -> {
                        if (isValidForm())
                            sendTransfer()
                    }
                    NewTransferContract.NewTransferUiEvent.CloseTransactionDialog ->
                        setState { copy(transferSuccess = false) }

                    is NewTransferContract.NewTransferUiEvent.ConvertTransferAmount ->
                        if (isValidForm())
                            convertAmount(it.amount)
                }
            }
        }
    }

    private fun isValidForm(): Boolean {
        if (state.value.amount==0 || state.value.amount.toString()=="") {
            setState {
                copy(
                    invalidFormError = true,
                    amountError = true,
                    amountErrorText = "Amount cannot be empty or zero."
                )
            }
        } else
            setState { copy(invalidFormError = false, amountError = false) }
        return !state.value.invalidFormError
    }



    private fun fetchAccounts() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val accountResponse = bankRepository.getAccounts()
                    setState { copy(accounts = accountResponse,
                        fromAccount = accountResponse.firstOrNull()?.accountNumber ?: "",
                        toAccount = if (accountResponse.size > 1)
                            accountResponse[1].accountNumber
                        else
                            accountResponse.firstOrNull()?.accountNumber ?: "") }
                }

            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error loading user info: ${e.message}")
            }
        }
    }

    private fun sendTransfer() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    bankRepository.sendTransfer(
                        NewTransferPostRequest(
                        senderAccountNumber = state.value.fromAccount,
                        receiverAccountNumber = state.value.toAccount,
                        amount = state.value.amount.toFloat(),
                        )
                    )
                }
                if (response)
                    setState { copy(transferSuccess = true,
                        error = null) }
                else {
                    setState { copy(error = "You reached over the account limit for transfer.") }
                }
                Log.d("raf", "POST for payment response: $response")

            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error transferring money: ${e.message}")
            }
        }
    }

    private fun convertAmount(amount: Int) {
        viewModelScope.launch {
            try {
                val fromAcc = state.value.accounts.find { it.accountNumber == state.value.fromAccount }
                val toAcc = state.value.accounts.find { it.accountNumber == state.value.toAccount }
                val converted = if (fromAcc != null && toAcc != null) {
                    if (fromAcc.currencyCode == toAcc.currencyCode) {
                        amount
                    } else {
                        val response = withContext(Dispatchers.IO) {
                            bankRepository.convertCurrency(fromAcc.currencyCode, toAcc.currencyCode)
                        }
                        amount * response.sellRate
                    }
                } else {
                    0f
                }
                setState { copy(convertedAmount = converted.toFloat()) }
            } catch (e: Exception) {
            setState { copy(error = e.message) }
            Log.e("raf", "Error converting Amount: ${e.message}")
        }
        }
    }
}
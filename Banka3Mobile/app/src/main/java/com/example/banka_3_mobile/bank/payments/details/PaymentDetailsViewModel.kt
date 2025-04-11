package com.example.banka_3_mobile.bank.payments.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.bank.loans.details.LoanDetailsContract
import com.example.banka_3_mobile.bank.repository.BankRepository
import com.example.banka_3_mobile.navigation.loanId
import com.example.banka_3_mobile.navigation.paymentId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PaymentDetailsViewModel @Inject constructor(
    private val bankRepository: BankRepository,
    savedStateHandle: SavedStateHandle
): ViewModel()
{
    private val paymentId: Long = savedStateHandle.paymentId.toLong()

    private val _state = MutableStateFlow(PaymentDetailsContract.PaymentDetailsUiState())
    val state = _state.asStateFlow()

    private fun setState(reducer: PaymentDetailsContract.PaymentDetailsUiState.()-> PaymentDetailsContract.PaymentDetailsUiState) =
        _state.getAndUpdate(reducer)

    init {
        fetchPaymentDetails(paymentId)
    }

    private fun fetchPaymentDetails(paymentId: Long) {
        viewModelScope.launch {
            try {
                setState { copy(fetching = true) }
                val payment = withContext(Dispatchers.IO) {
                    bankRepository.getPaymentDetails(paymentId)
                }
                setState { copy(payment = payment) }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error Fetching Payment Details: ${e.message}")
            } finally {
                setState { copy(fetching = false) }
            }
        }
    }
}
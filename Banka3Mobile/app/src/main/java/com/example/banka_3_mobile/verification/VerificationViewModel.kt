package com.example.banka_3_mobile.verification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class VerificationViewModel @Inject constructor(
    private val bankRepository: BankRepository
): ViewModel()
{
    private val _state = MutableStateFlow(VerificationContract.VerificationUiState())
    val state: StateFlow<VerificationContract.VerificationUiState> = _state.asStateFlow()

    private fun setState(reducer: VerificationContract.VerificationUiState.() -> VerificationContract.VerificationUiState) =
        _state.update(reducer)

    private  val events = MutableSharedFlow<VerificationContract.VerificationUIEvent>()
    fun setEvent(event: VerificationContract.VerificationUIEvent) = viewModelScope.launch {
        events.emit(event)
    }

    init {
        fetchPayments()
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect {
                when (it) {
                    VerificationContract.VerificationUIEvent.DeclinePending -> TODO()
                    VerificationContract.VerificationUIEvent.VerifyPending -> TODO()
                }
            }
        }
    }

    private fun fetchPayments() {
        viewModelScope.launch {
            try {
                val payments = withContext(Dispatchers.IO) {
                    bankRepository.getPayments()
                }
                setState { copy (allPaymentsRequests = payments.content) }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error fetching payments: ${e.message}")
            } finally {
                setState { copy(fetching = false) }
                Log.d("raf", "Fetching: false")
            }

        }
    }

}
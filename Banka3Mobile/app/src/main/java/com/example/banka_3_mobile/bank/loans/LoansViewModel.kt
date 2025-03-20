package com.example.banka_3_mobile.bank.loans

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.bank.repository.BankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoansViewModel @Inject constructor(
    private val bankRepository: BankRepository
): ViewModel()
{
    private val _state = MutableStateFlow(LoansContract.LoansUiState())
    val state = _state.asStateFlow()

    private fun setState(reducer: LoansContract.LoansUiState.()-> LoansContract.LoansUiState) =
        _state.getAndUpdate(reducer)

    init {
        fetchLoans()
        fetchLoanRequests()
    }

    private fun fetchLoans() {
        viewModelScope.launch {
            try {
                setState { copy(fetching = true) }
                val loans = withContext(Dispatchers.IO) {
                    bankRepository.getLoans()
                }
                setState { copy(loans = loans.content) }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error Fetching Loans: ${e.message}")
            } finally {
                setState { copy(fetching = false) }
            }
        }
    }

    private fun fetchLoanRequests() {
        viewModelScope.launch {
            try {
                setState { copy(fetching = true) }
                val loanRequests = withContext(Dispatchers.IO) {
                    bankRepository.getLoanRequests()
                }
                setState { copy(loanRequests = loanRequests.content) }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error Fetching Loan Requests: ${e.message}")
            } finally {
                setState { copy(fetching = false) }
            }
        }
    }
}
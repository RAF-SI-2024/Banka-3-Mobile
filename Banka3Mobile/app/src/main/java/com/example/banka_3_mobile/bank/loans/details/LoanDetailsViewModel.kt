package com.example.banka_3_mobile.bank.loans.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.bank.loans.LoansContract
import com.example.banka_3_mobile.bank.repository.BankRepository
import com.example.banka_3_mobile.navigation.loanId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoanDetailsViewModel @Inject constructor(
    private val bankRepository: BankRepository,
    savedStateHandle: SavedStateHandle
): ViewModel()
{
    private val loanId: Long = savedStateHandle.loanId.toLong()

    private val _state = MutableStateFlow(LoanDetailsContract.LoanDetailsUiState())
    val state = _state.asStateFlow()

    private fun setState(reducer: LoanDetailsContract.LoanDetailsUiState.()-> LoanDetailsContract.LoanDetailsUiState) =
        _state.getAndUpdate(reducer)

    init {
        fetchLoanDetails(loanId)
        fetchLoanInstallments(loanId)
    }

    private fun fetchLoanDetails(loanId: Long) {
        viewModelScope.launch {
            try {
                setState { copy(fetching = true) }
                val loan = withContext(Dispatchers.IO) {
                    bankRepository.getLoanDetails(loanId)
                }
                setState { copy(loan = loan) }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error Fetching Loan Details: ${e.message}")
            } finally {
                setState { copy(fetching = false) }
            }
        }
    }

    private fun fetchLoanInstallments(loanId: Long) {
        viewModelScope.launch {
            try {
                setState { copy(fetching = true) }
                val loanInstallments = withContext(Dispatchers.IO) {
                    bankRepository.getLoanInstallments(loanId)
                }
                setState { copy(loanInstallments = loanInstallments) }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error Fetching Loans: ${e.message}")
            } finally {
                setState { copy(fetching = false) }
            }
        }
    }
}
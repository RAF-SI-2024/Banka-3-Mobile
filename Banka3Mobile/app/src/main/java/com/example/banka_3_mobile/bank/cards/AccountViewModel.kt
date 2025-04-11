package com.example.banka_3_mobile.bank.cards

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.bank.payments.model.PaymentStatus
import com.example.banka_3_mobile.bank.repository.BankRepository
import com.example.banka_3_mobile.navigation.accountNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bankRepository: BankRepository
): ViewModel()
{
    private val accountNumber: String = savedStateHandle.accountNumber

    private val _state = MutableStateFlow(
        AccountContract.CardsUiState(
            accountNumber = accountNumber
        )
    )
    val state = _state.asStateFlow()
    private fun setState(reducer: AccountContract.CardsUiState.()-> AccountContract.CardsUiState) =
        _state.getAndUpdate(reducer)

    init {
        fetchCards()
        fetchPayments()
        fetchAccounts()
    }

    private fun fetchCards() {
        viewModelScope.launch {
            try {
                val cards = withContext(Dispatchers.IO) {
                    bankRepository.getCardsOfAccount(accountNumber)
                }
                setState { copy(cardList = cards) }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error Fetching Cards: ${e.message}")
            } finally {
                setState { copy(fetching = false) }
            }
        }
    }

    private fun fetchPayments() {
        viewModelScope.launch {
            try {
                val payments = withContext(Dispatchers.IO) {
                    bankRepository.getPaymentsByAccount(accountNumber = accountNumber, size = 20)
                }
                setState { copy(payments = payments.content.filter { it.senderName!=null && it.status==PaymentStatus.COMPLETED }.reversed()) }
                Log.d("raf", "payments: $payments")
            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error Fetching Payments: ${e.message}")
            } finally {
                setState { copy(fetching = false) }
            }
        }
    }

    private fun fetchAccounts() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val accountResponse = bankRepository.getAccounts()
                    setState { copy(accounts = accountResponse) }
                    setState { copy(currentAccount = accounts.filter { it.accountNumber == accountNumber }.get(0)) }
                }

            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error loading user info: ${e.message}")
            }
        }
    }
}
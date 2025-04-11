package com.example.banka_3_mobile.bank.cards.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.bank.repository.BankRepository
import com.example.banka_3_mobile.navigation.accountNumber
import com.example.banka_3_mobile.navigation.cardNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CardDetailsViewModel @Inject constructor(
    private val bankRepository: BankRepository,
    savedStateHandle: SavedStateHandle
): ViewModel()
{

    private val accountNumber: String = savedStateHandle.accountNumber
    private val cardNumber: String = savedStateHandle.cardNumber

    private val _state = MutableStateFlow(CardDetailsContract.CardDetailsUiState(
        accountNumber = accountNumber,
        cardNumber = cardNumber
    ))
    val state: StateFlow<CardDetailsContract.CardDetailsUiState> = _state.asStateFlow()

    private fun setState(reducer: CardDetailsContract.CardDetailsUiState.() -> CardDetailsContract.CardDetailsUiState) =
        _state.update(reducer)


    init {
        fetchCard()
    }

    private fun fetchCard() {
        viewModelScope.launch {
            try {
                val cards = withContext(Dispatchers.IO) {
                    bankRepository.getCardsOfAccount(accountNumber)
                }
                val card = cards.find { it.cardNumber == cardNumber }
                setState { copy(card = card) }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error Fetching Card: ${e.message}")
            } finally {
                setState { copy(fetching = false) }
            }
        }
    }


}
package com.example.banka_3_mobile.bank.payments.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.bank.repository.BankRepository
import com.example.banka_3_mobile.home.HomeContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PaymentHomeViewModel @Inject constructor(
    private val bankRepository: BankRepository
): ViewModel()
{
    private val _state = MutableStateFlow(PaymentHomeContract.PaymentHomeUiState())
    val state: StateFlow<PaymentHomeContract.PaymentHomeUiState> = _state.asStateFlow()

    private fun setState(reducer: PaymentHomeContract.PaymentHomeUiState.() -> PaymentHomeContract.PaymentHomeUiState) =
        _state.update(reducer)

    private  val events = MutableSharedFlow<PaymentHomeContract.PaymentHomeUiEvent>()

    fun setEvent(event: PaymentHomeContract.PaymentHomeUiEvent) = viewModelScope.launch {
        events.emit(event)
    }

    init {
        fetchExchangeRates()
        observeExchangeInput()
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect {
                when (it) {
                    PaymentHomeContract.PaymentHomeUiEvent.CloseConversionDialog ->
                        setState { copy(showConversionDialog = false) }
                    PaymentHomeContract.PaymentHomeUiEvent.CloseExchangeRateDialog ->
                        setState { copy(showExchangeRatesDialog = false) }
                    is PaymentHomeContract.PaymentHomeUiEvent.ConvertValue ->
                        setState { copy(exchangeInput = it.exchangeInput) }
                    PaymentHomeContract.PaymentHomeUiEvent.OpenConversionDialog ->
                        setState { copy(showConversionDialog = true) }
                    PaymentHomeContract.PaymentHomeUiEvent.OpenExchangeRateDialog ->
                        setState { copy(showExchangeRatesDialog = true) }

                    is PaymentHomeContract.PaymentHomeUiEvent.ToSymbolSelected ->
                        setState { copy(selectedToExchangeRate = it.symbol) }
                    is PaymentHomeContract.PaymentHomeUiEvent.FromSymbolSelected ->
                        setState { copy(selectedFromExchangeRate = it.symbol) }
                }
            }
        }
    }

    private fun fetchExchangeRates() {
        viewModelScope.launch {
            try {
                val exchangeRates = withContext(Dispatchers.IO) {
                    bankRepository.getExchangeRates()
                }
                val symbols = exchangeRates.map { it.toCurrency.code }.distinct()
                setState {
                    copy(
                        exchangeRates = exchangeRates,
                        availableSymbols = symbols,
                    )
                }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error Fetching Payments: ${e.message}")
            }
        }
    }


    @OptIn(FlowPreview::class)
    private fun observeExchangeInput() {
        viewModelScope.launch {
            events
                .filterIsInstance<PaymentHomeContract.PaymentHomeUiEvent.ConvertValue>()
                .debounce(200)
                .collect {
                    if (state.value.selectedFromExchangeRate!=state.value.selectedToExchangeRate) {
                        val exchangeResponse = withContext(Dispatchers.IO) {
                            bankRepository.convertCurrency(state.value.selectedFromExchangeRate, state.value.selectedToExchangeRate)
                        }
                        setState { copy(exchangeValue = state.value.exchangeInput * exchangeResponse.exchangeRate, exchangeInput = exchangeInput) }

                    } else {
                        setState { copy(exchangeValue = state.value.exchangeInput.toFloat(), exchangeInput = exchangeInput) }
                    }
                }

        }

    }
}
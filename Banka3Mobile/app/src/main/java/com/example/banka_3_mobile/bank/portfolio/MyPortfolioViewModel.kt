package com.example.banka_3_mobile.bank.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.bank.payments.transfer.NewTransferContract
import com.example.banka_3_mobile.bank.portfolio.MyPortfolioContract.MyPortfolioUiState
import com.example.banka_3_mobile.bank.repository.BankRepository
import com.example.banka_3_mobile.verification.VerificationContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPortfolioViewModel @Inject constructor(
    private val bankRepository: BankRepository
): ViewModel()
{
    private val _state = MutableStateFlow(MyPortfolioUiState())
    val state: StateFlow<MyPortfolioUiState> = _state.asStateFlow()

    private fun setState(reducer: MyPortfolioUiState.() -> MyPortfolioUiState) =
        _state.update(reducer)

    private  val events = MutableSharedFlow<MyPortfolioContract.MyPortfolioUIEvent>()
    fun setEvent(event: MyPortfolioContract.MyPortfolioUIEvent) = viewModelScope.launch {
        events.emit(event)
    }

    init {
       // fetchSecurities()
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect {
                when (it) {
                    MyPortfolioContract.MyPortfolioUIEvent.OpenProfitInfoDialog ->
                        setState { copy(showProfitInfoDialog = true) }

                    MyPortfolioContract.MyPortfolioUIEvent.CloseProfitInfoDialog ->
                        setState { copy(showProfitInfoDialog = false) }

                    MyPortfolioContract.MyPortfolioUIEvent.OpenTaxInfoDialog ->
                        setState { copy(showTaxInfoDialog = true) }

                    MyPortfolioContract.MyPortfolioUIEvent.CloseTaxInfoDialog ->
                        setState { copy(showTaxInfoDialog = false) }
                }
            }
        }
    }

}
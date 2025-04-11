package com.example.banka_3_mobile.bank.payments.payee

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.bank.payments.payee.model.PayeeGetResponse
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
class PayeeViewModel @Inject constructor(
    private val bankRepository: BankRepository
): ViewModel()
{
    private val _state = MutableStateFlow(PayeeInterface.PayeeUiState())
    val state: StateFlow<PayeeInterface.PayeeUiState> = _state.asStateFlow()

    private fun setState(reducer: PayeeInterface.PayeeUiState.() -> PayeeInterface.PayeeUiState) =
        _state.update(reducer)

    private  val events = MutableSharedFlow<PayeeInterface.PayeeUiEvent>()

    fun setEvent(event: PayeeInterface.PayeeUiEvent) = viewModelScope.launch {
        events.emit(event)
    }

    init {
        fetchPayees()
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is PayeeInterface.PayeeUiEvent.TypingAccountNumber -> {
                        setState {
                            copy(accountNumberField = event.accountNumber.toString())
                        }
                        if (state.value.accountNumberField.length<8) {
                            setState { copy(accountNumberErrorText = "Account number should be at least 8 digits long.") }
                            setState { copy(accountNumberError = true) }
                        }
                        else {
                            setState { copy(accountNumberError = false) }
                        }
                    }

                    is PayeeInterface.PayeeUiEvent.TypingName -> {
                        setState { copy(nameField = event.name) }
                        if (state.value.nameField=="" || state.value.nameField.contains("[0-9]".toRegex())) {
                            setState { copy(nameErrorText = "Name cannot be empty or contain digits.") }
                            setState { copy(nameError = true) }
                        } else {
                            setState { copy(nameError = false) }
                        }
                    }

                    is PayeeInterface.PayeeUiEvent.OpenAddDialog ->
                        setState { copy(addDialogOpen = true) }
                    is PayeeInterface.PayeeUiEvent.CloseAddDialog ->
                        setState { copy(addDialogOpen = false, nameField = "", accountNumberField = "") }
                    is PayeeInterface.PayeeUiEvent.AddPayee -> {
                        if (isValidForm())
                          addPayee()
                    }
                    is PayeeInterface.PayeeUiEvent.OpenEditDialog -> {

                            val payee = state.value.payees.find { it.id == event.id }
                            if (payee != null) {
                                setState {
                                    copy(
                                        editDialogOpen = true,
                                        idSelected = payee.id ?: -1,
                                        nameField = payee.name,
                                        accountNumberField = payee.accountNumber
                                    )
                                }
                            }

                    }
                    is PayeeInterface.PayeeUiEvent.CloseEditDialog ->
                        setState { copy(editDialogOpen = false, nameField = "", accountNumberField = "") }
                    is PayeeInterface.PayeeUiEvent.UpdatePayee -> {
                        if (isValidForm())
                            updatePayee()
                    }
                    is PayeeInterface.PayeeUiEvent.OpenDeleteDialog -> {
                        val payee = state.value.payees.find { it.id == event.id }
                        if (payee != null) {
                            setState {
                                copy(
                                    deleteDialogOpen = true,
                                    idSelected = payee.id ?: -1,
                                    nameField = payee.name,
                                    accountNumberField = payee.accountNumber
                                )
                            }
                        }
                    }
                    is PayeeInterface.PayeeUiEvent.CloseDeleteDialog ->
                        setState { copy(deleteDialogOpen = false, nameField = "", accountNumberField = "") }
                    is PayeeInterface.PayeeUiEvent.DeletePayee -> {
                        if (isValidForm())
                          deletePayee()
                    }
                }
            }
        }
    }

    private fun isValidForm(): Boolean {
        if ((state.value.accountNumberField.length<8)) {
            setState { copy(invalidForm = true,
                accountNumberError = true) }
        } else if (state.value.nameField=="" || state.value.nameField.contains("[0-9]".toRegex())) {
            setState { copy(invalidForm = true,
                nameError = true) }
        }
        else {
            setState { copy(invalidForm = false) }
        }
        return !state.value.invalidForm
    }

    private fun fetchPayees() {
        viewModelScope.launch {
            try {
                val payees = withContext(Dispatchers.IO) {
                    bankRepository.getPayees()
                }
                setState { copy(payees = payees) }

            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error loading payees: ${e.message}")
            }
        }
    }

    private fun addPayee() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    bankRepository.addPayee(PayeeGetResponse(name = state.value.nameField, accountNumber = state.value.accountNumberField))
                }
                setState { copy(addDialogOpen = false,
                    idSelected = -1,
                    nameField = "",
                    accountNumberField = "") }
                withContext(Dispatchers.IO) {
                    fetchPayees()
                }
                Log.d("raf", "POST response for add payees: $response")
            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error adding payees: ${e.message}")
            }
        }
    }

    private fun updatePayee() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    bankRepository.updatePayee(state.value.idSelected, PayeeGetResponse(state.value.idSelected, state.value.nameField, state.value.accountNumberField))
                }
                setState { copy(editDialogOpen = false,
                    nameField = "",
                    accountNumberField = "") }
                withContext(Dispatchers.IO) {
                    fetchPayees()
                }
                Log.d("raf", "PUT response for update payees: $response")
            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error updating payee: ${e.message}")
            }
        }
    }

    private fun deletePayee() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    bankRepository.deletePayee(state.value.idSelected)
                }
                setState { copy(deleteDialogOpen = false,
                    nameField = "",
                    accountNumberField = "") }
                withContext(Dispatchers.IO) {
                    fetchPayees()
                }
                Log.d("raf", "DELETE response for delete payees: $response")

            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error deleting payee: ${e.message}")
            }
        }
    }

}
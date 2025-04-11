package com.example.banka_3_mobile.bank.payments.payee

import com.example.banka_3_mobile.bank.payments.payee.model.PayeeGetResponse

interface PayeeInterface {
    data class PayeeUiState(
        val payees: List<PayeeGetResponse> = emptyList(),
        val fetching: Boolean = true,
        val error: String? = null,
        val idSelected: Long = -1,
        val accountNumberField: String = "",
        val nameField: String = "",
        val editDialogOpen: Boolean = false,
        val addDialogOpen: Boolean = false,
        val deleteDialogOpen: Boolean = false,
        val nameErrorText: String = "",
        val accountNumberErrorText: String = "",
        val nameError: Boolean = false,
        val accountNumberError: Boolean = false,
        val invalidForm: Boolean =  false,
    )

    sealed class PayeeUiEvent {
        data class TypingAccountNumber(val accountNumber: String): PayeeUiEvent()
        data class TypingName(val name: String): PayeeUiEvent()
        data class OpenDeleteDialog(val id: Long): PayeeUiEvent()
        data class OpenEditDialog(val id: Long): PayeeUiEvent()
        data object CloseEditDialog: PayeeUiEvent()
        data object CloseDeleteDialog: PayeeUiEvent()
        data object OpenAddDialog: PayeeUiEvent()
        data object CloseAddDialog: PayeeUiEvent()
        data object AddPayee: PayeeUiEvent()
        data object DeletePayee: PayeeUiEvent()
        data object UpdatePayee: PayeeUiEvent()
    }
}
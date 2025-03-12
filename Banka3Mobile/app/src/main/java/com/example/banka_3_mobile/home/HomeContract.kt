package com.example.banka_3_mobile.home

import com.example.banka_3_mobile.user.model.ClientGetResponse

interface HomeContract {
    data class HomeUiState (
        val loggedIn: Boolean = true,
        val email: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val client: ClientGetResponse? = null,
        val error: String? = null,
        val fetching: Boolean = true,
        val showLogoutDialog: Boolean = false
    )

    sealed class HomeUIEvent {
        data object ShowLogoutDialog: HomeUIEvent()
        data object CloseLogoutDialog: HomeUIEvent()
        data object Logout: HomeUIEvent()
    }
}
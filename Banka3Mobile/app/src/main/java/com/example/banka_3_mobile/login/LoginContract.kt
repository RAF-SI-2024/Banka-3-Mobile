package com.example.banka_3_mobile.login

interface LoginContract {
    data class LoginUiState (
        val loggedIn: Boolean = false,
        val email: String = "",
        val password: String = "",
        val incorrectEmailFormat: Boolean = false,
        val incorrectPasswordFormat: Boolean = false,
        val response: String = "",
        val error: Throwable? = null,
    )

    sealed class LoginUIEvent {
        data object Login: LoginUIEvent()
        data class TypingEmail(val email: String): LoginUIEvent()
        data class TypingPassword(val password: String): LoginUIEvent()
    }
}
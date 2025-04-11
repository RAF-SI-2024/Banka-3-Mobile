package com.example.banka_3_mobile.navigation

interface AppNavigationContract {
    data class AppNavigationUiState (
        val activeRequestCount: Int = 0,
        val error: String? = null,
    )

    /**
     * TODO eventovi
     */
}
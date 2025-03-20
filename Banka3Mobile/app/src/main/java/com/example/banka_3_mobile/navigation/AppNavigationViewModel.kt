package com.example.banka_3_mobile.navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.user.repository.UserRepository
import com.example.banka_3_mobile.verification.VerificationContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AppNavigationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AppNavigationContract.AppNavigationUiState())
    val state: StateFlow<AppNavigationContract.AppNavigationUiState> = _state.asStateFlow()

    private fun setState(reducer: AppNavigationContract.AppNavigationUiState.() -> AppNavigationContract.AppNavigationUiState) =
        _state.update(reducer)


    init {
     //   startPeriodicRefresh()
    }

    private fun startPeriodicRefresh() {
        viewModelScope.launch {
            while (isActive) {
                try {
                    val activeRequests = withContext(Dispatchers.IO) {
                        userRepository.getActiveVerificationRequests()
                    }
                    setState { copy(activeRequestCount = activeRequests.size) }
                } catch (e: Exception) {
                    setState { copy(error = e.message) }
                    Log.e("raf", "Error fetching payments: ${e.message}")
                }
                delay(60_000L)
            }
        }
    }

}

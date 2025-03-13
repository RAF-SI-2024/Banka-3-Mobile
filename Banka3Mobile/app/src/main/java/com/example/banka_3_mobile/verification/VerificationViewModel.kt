package com.example.banka_3_mobile.verification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.bank.repository.BankRepository
import com.example.banka_3_mobile.user.repository.UserRepository
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
class VerificationViewModel @Inject constructor(
    private val bankRepository: BankRepository,
    private val userRepository: UserRepository
): ViewModel()
{
    private val _state = MutableStateFlow(VerificationContract.VerificationUiState())
    val state: StateFlow<VerificationContract.VerificationUiState> = _state.asStateFlow()

    private fun setState(reducer: VerificationContract.VerificationUiState.() -> VerificationContract.VerificationUiState) =
        _state.update(reducer)

    private  val events = MutableSharedFlow<VerificationContract.VerificationUIEvent>()
    fun setEvent(event: VerificationContract.VerificationUIEvent) = viewModelScope.launch {
        events.emit(event)
    }

    init {
        fetchRequests()
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect {
                when (it) {
                    is VerificationContract.VerificationUIEvent.DeclinePending -> declinePendingRequest(it.id)
                    is VerificationContract.VerificationUIEvent.VerifyPending -> acceptPendingRequest(it.id)
                    VerificationContract.VerificationUIEvent.PullToRefreshTrigger -> onPullToRefreshRequest()
                }
            }
        }
    }

    private fun fetchRequests() {
        viewModelScope.launch {
            try {
                val activeRequests = withContext(Dispatchers.IO) {
                    userRepository.getActiveVerificationRequests()
                }
                val requestHistory = withContext(Dispatchers.IO) {
                    userRepository.getVerificationHistory()
                }
                setState { copy (activeRequests = activeRequests,
                    requestHistory = requestHistory) }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error fetching payments: ${e.message}")
            } finally {
                setState { copy(fetching = false) }
                Log.d("raf", "Fetching: false")
            }

        }
    }

    private fun acceptPendingRequest(id: Long){
        viewModelScope.launch {
            try {
                userRepository.acceptVerificationRequest(id)
                fetchRequests()
            }catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error fetching payments: ${e.message}")
            }
        }
    }

    private fun declinePendingRequest(id: Long) {
        viewModelScope.launch {
            try {
                userRepository.denyVerificationRequest(id)
                fetchRequests()
            }catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error fetching payments: ${e.message}")
            }
        }
    }

    private fun onPullToRefreshRequest() {
        setState { copy(isRefreshing = true)}
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    fetchRequests()
                }
                setState { copy(isRefreshing = true)}
            }
            catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error fetching payments: ${e.message}")
            } finally {
                setState { copy(isRefreshing = false)}
            }
        }
    }

}
package com.example.banka_3_mobile.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.login.LoginContract
import com.example.banka_3_mobile.user.account.datastore.AccountData
import com.example.banka_3_mobile.user.account.datastore.AccountDataStore
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
class HomeViewModel @Inject constructor(
    private val dataStore: AccountDataStore,
    private val userRepository: UserRepository
): ViewModel()
{
    private val _state = MutableStateFlow(HomeContract.HomeUiState())
    val state: StateFlow<HomeContract.HomeUiState> = _state.asStateFlow()

    private fun setState(reducer: HomeContract.HomeUiState.() -> HomeContract.HomeUiState) =
        _state.update(reducer)

    private  val events = MutableSharedFlow<HomeContract.HomeUIEvent>()
    fun setEvent(event: HomeContract.HomeUIEvent) = viewModelScope.launch {
        events.emit(event)
    }

    init {
        loadUserInfo()
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect {
                when (it) {
                    HomeContract.HomeUIEvent.Logout -> logout()
                    HomeContract.HomeUIEvent.CloseLogoutDialog -> setState { copy(showLogoutDialog = false) }
                    HomeContract.HomeUIEvent.ShowLogoutDialog -> setState { copy(showLogoutDialog = true) }
                }
            }
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            try {
                setState { copy(fetching = true) }
                withContext(Dispatchers.IO) {
                    val clientResponse = userRepository.getUser()
                    clientResponse.birthDate?.let { Log.d("raf", it) }
                    setState { copy(client = clientResponse) }
                }

            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error loading user info: ${e.message}")
            } finally {
                setState { copy(fetching = false) }
                Log.d("raf", "Fetching: false")
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                setState { copy(showLogoutDialog = false) }
                withContext(Dispatchers.IO) {
                    dataStore.updateProfileData(AccountData.EMPTY)
                }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
                Log.e("raf", "Error logging out: ${e.message}")
            } finally {
                setState { copy(loggedIn = false) }
                //Log.d("raf", "Fetching: false")
            }
        }
    }
}
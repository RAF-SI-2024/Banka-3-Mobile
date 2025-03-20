package com.example.banka_3_mobile.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banka_3_mobile.user.account.datastore.AccountData
import com.example.banka_3_mobile.user.account.datastore.AccountDataStore
import com.example.banka_3_mobile.user.jwt.isClientToken
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
class LoginViewModel @Inject constructor(
    private val accountDataStore: AccountDataStore,
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(LoginContract.LoginUiState())
    val state: StateFlow<LoginContract.LoginUiState> = _state.asStateFlow()

    private fun setState(reducer: LoginContract.LoginUiState.() -> LoginContract.LoginUiState) =
        _state.update(reducer)

    private  val events = MutableSharedFlow<LoginContract.LoginUIEvent>()
    fun setEvent(event: LoginContract.LoginUIEvent) = viewModelScope.launch {
        events.emit(event)
    }

    init {
        Log.d("raf", "Ulazim u init logina")
        if (accountDataStore.data.value.email == "" ||
            accountDataStore.data.value.password == ""
        )
            observeEvents()
        else {

            setState { copy(
                email = accountDataStore.data.value.email,
                password = accountDataStore.data.value.password) }

            checkOrUpdateToken()
            setState{copy(loggedIn = true)}
        }

    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect {
                when (it) {
                    LoginContract.LoginUIEvent.Login -> validate()
                    is LoginContract.LoginUIEvent.TypingEmail -> {
                        setState { copy(email = it.email) }
                        if(!checkEmailValidity())
                            setState { copy(incorrectEmailFormat = true) }
                        else setState { copy(incorrectEmailFormat = false) }
                    }
                    is LoginContract.LoginUIEvent.TypingPassword -> {
                        setState { copy(password = it.password) }
                        if (state.value.password.length < 3) {
                            setState { copy(incorrectPasswordFormat = true) }
                        } else setState { copy(incorrectPasswordFormat = false) }

                    }
                }
            }
        }
    }

    private fun validate() {
        val emailValid = checkEmailValidity()
        val passwordValid = state.value.password.length >= 3

        if (!emailValid || !passwordValid) {
            setState { copy(
                incorrectEmailFormat = !emailValid,
                incorrectPasswordFormat = !passwordValid
            ) }
        }
        else {

            viewModelScope.launch(Dispatchers.IO) {
                try {
                        val response =
                            userRepository.login(email = state.value.email,
                                password = state.value.password)

                        if (!isClientToken(response.token)) {
                            setState { copy(error = IllegalArgumentException("Invalid client login credentials")) }
                            Log.e("raf", state.value.error?.message!!)
                            Log.d("raf", state.value.error?.message!!)
                            return@launch
                        }

                        setState { copy(response = response.token, error = null) }
                        accountDataStore.updateProfileData(
                            AccountData(
                                email = state.value.email,
                                token = response.token,
                                password = state.value.password
                            )
                        )
                    
                    setState { copy(loggedIn = true) }
              
                } catch (e: Exception) {
                    setState { copy(error = e) }
                    Log.e("raf", e.message!!)
                }
            }
        }
    }

    private fun checkEmailValidity(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(state.value.email).matches()
    }

    private fun checkOrUpdateToken() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.IO) {
                   // val validity = userRepository.checkToken(accountDataStore.data.value.token)
                  //  if (!validity) {
                        val response =
                            userRepository.login(email = state.value.email,
                                password = state.value.password)
                        Log.d("raf", "Pozvan update token dobijen: ${response.token}")
                        setState { copy(response = response.token) }
                        accountDataStore.updateProfileData(
                            AccountData(
                                email = state.value.email,
                                token = response.token,
                                password = state.value.password
                            )
                        )
                    //}
                }
                setState { copy(loggedIn = true) }
            } catch (e: Exception) {
                setState { copy(error = e) }
                Log.e("raf", e.message!!)
            }
        }

    }


}
package com.example.banka_3_mobile.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.loginPage(
    route: String,
    navController: NavController,
) = composable(
    route = route,
) {navBackStackEntry ->

    val loginViewModel = hiltViewModel<LoginViewModel>(navBackStackEntry)
    val state by loginViewModel.state.collectAsState()

    LaunchedEffect(state.loggedIn) {
        if (state.loggedIn) {
            navController.navigate("home") {
                popUpTo(route) { inclusive = true }
            }
        }
    }

    LoginScreen(
        state = state,
        eventPublisher = {
            loginViewModel.setEvent(it)
        }
    )
}

@Composable
fun LoginScreen(
    state: LoginContract.LoginUiState,
    eventPublisher: (uiEvent: LoginContract.LoginUIEvent) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        content = {
           // when {
                /*state.error != null ->
                    ErrorScreen(message = state.error.message!!)*/
           //     else ->
                    LoginColumn(state = state, eventPublisher = eventPublisher)
           // }
        })
}

@Composable
fun LoginColumn(
    state: LoginContract.LoginUiState,
    eventPublisher: (uiEvent: LoginContract.LoginUIEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Banka 3",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { eventPublisher(LoginContract.LoginUIEvent.TypingEmail(it)) },
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        if (state.incorrectEmailFormat) {
            Text(
                text = "Invalid email format.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Start)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { eventPublisher(LoginContract.LoginUIEvent.TypingPassword(it)) },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(8.dp)
        )
        if (state.incorrectPasswordFormat) {
            Text(
                text = "Password can't be shorter than 3 characters.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Start)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (state.error!=null) {
            state.error.message?.let {
                /*Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.ExtraBold
                )*/
                if (state.error.message=="HTTP 401 ") {
                    Text(
                        text = "Invalid login credentials for client account.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.ExtraBold
                    )
                } else {
                    Text(
                        text = "Unknown error, please try again later.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
        Button(
            onClick = { eventPublisher(LoginContract.LoginUIEvent.Login) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Login", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
        }


      //  if (state.response!="") {
      //      Text(state.response)
      //  }

    }
}
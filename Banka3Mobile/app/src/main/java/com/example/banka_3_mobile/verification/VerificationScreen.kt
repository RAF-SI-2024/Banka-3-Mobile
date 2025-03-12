package com.example.banka_3_mobile.verification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.verificationPage(
    route: String,
    navController: NavController,
) = composable(
    route = route,
) {navBackStackEntry ->

    val verificationViewModel = hiltViewModel<VerificationViewModel>(navBackStackEntry)
    val state by verificationViewModel.state.collectAsState()

    VerificationScreen(
        state = state,
        eventPublisher = {
            verificationViewModel.setEvent(it)
        },
        onClose = {
            navController.navigateUp()
        }
    )
}

@Composable
fun VerificationScreen(
    state: VerificationContract.VerificationUiState,
    eventPublisher: (uiEvent: VerificationContract.VerificationUIEvent) -> Unit,
    onClose: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        content = {

            when {
                state.fetching ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(modifier = Modifier.align(Alignment.Center), text = "Loading payments...")
                    }
                state.error != null ->
                    Text(color = Color.Red, text = "${state.error}")
                else -> {
                    VerifyColumn(state = state, eventPublisher = eventPublisher, onClose = onClose)
                }
            }
        })
}

@Composable
fun VerifyColumn(
    state: VerificationContract.VerificationUiState,
    eventPublisher: (uiEvent: VerificationContract.VerificationUIEvent) -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp).padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VerifyHeader(
            state = state,
            eventPublisher = eventPublisher,
            onClose = onClose
        )
    }
}

@Composable
fun VerifyHeader(
    state: VerificationContract.VerificationUiState,
    eventPublisher: (uiEvent: VerificationContract.VerificationUIEvent) -> Unit,
    onClose: () -> Unit,
)  {
    Row(modifier = Modifier
        .fillMaxWidth()
        //.border(BorderStroke(2.dp, SolidColor(Color.Red)))
        ){
        IconButton(onClick = { onClose()}) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
        }
    }
    Text(text = "All payments will be listed here.", fontWeight = FontWeight.Bold, fontSize = 26.sp)
}
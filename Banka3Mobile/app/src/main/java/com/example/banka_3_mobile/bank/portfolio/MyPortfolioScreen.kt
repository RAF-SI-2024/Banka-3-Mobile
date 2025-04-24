package com.example.banka_3_mobile.bank.portfolio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.banka_3_mobile.bank.portfolio.model.SecurityMock


fun NavGraphBuilder.myPortfolioPage(
    route: String,
    navController: NavController,
) = composable(route = route) { navBackStackEntry ->
    val myPortfolioViewModel = hiltViewModel<MyPortfolioViewModel>(navBackStackEntry)
    val state by myPortfolioViewModel.state.collectAsState()
    MyPortfolioScreen(
        state = state,
        eventPublisher = { myPortfolioViewModel.setEvent(it) },
        onClose = { navController.navigateUp() }
    )
}


@Composable
fun MyPortfolioScreen(
    state: MyPortfolioContract.MyPortfolioUiState,
    eventPublisher: (MyPortfolioContract.MyPortfolioUIEvent) -> Unit,
    onClose: () -> Unit
) {
    val totalProfit = state.securities.sumOf { it.profit.toDouble() }.toFloat()
    val tax = totalProfit * 0.15f

    Surface(modifier = Modifier.fillMaxSize().padding(bottom = 50.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp).padding(top = 32.dp, bottom = 80.dp),
           // horizontalAlignment = Alignment.CenterHorizontally,
           // verticalArrangement = Arrangement.Center
        ) {



            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "My Portfolio",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = { eventPublisher(MyPortfolioContract.MyPortfolioUIEvent.OpenProfitInfoDialog) }) {
                    Text("Profit Info", style = MaterialTheme.typography.titleMedium)
                }
                Button(onClick = { eventPublisher(MyPortfolioContract.MyPortfolioUIEvent.OpenTaxInfoDialog) }) {
                    Text("Tax Info", style = MaterialTheme.typography.titleMedium)
                }
            }

            /*Row(modifier = Modifier.fillMaxWidth()) {
                Text("Type", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("Ticker", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("Amount", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("Price", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("Profit", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("Last Modified", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("Public", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            }*/

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(state.securities) { security ->
                    SecurityCard(security = security)
                    /*Row(modifier = Modifier.fillMaxWidth()) {
                        Text(security.type, modifier = Modifier.weight(1f))
                        Text(security.ticker, modifier = Modifier.weight(1f))
                        Text(security.amount.toString(), modifier = Modifier.weight(1f))
                        Text(security.price.toString(), modifier = Modifier.weight(1f))
                        Text(security.profit.toString(), modifier = Modifier.weight(1f))
                        Text(security.lastModified, modifier = Modifier.weight(1f))
                        Text(security.publicCounter.toString(), modifier = Modifier.weight(1f))
                    }*/
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }


        if (state.showProfitInfoDialog) {
            AlertDialog(
                onDismissRequest = {
                    eventPublisher(MyPortfolioContract.MyPortfolioUIEvent.CloseProfitInfoDialog)
                },
                title = { Text("Total Profit", style = MaterialTheme.typography.headlineLarge) },
                text = {
                    Column {
                        Text("The sum of all profits is:", style = MaterialTheme.typography.bodyLarge, fontSize = 18.sp)
                        Text(totalProfit.toString(), style = MaterialTheme.typography.headlineSmall)
                    }
                       },
                confirmButton = {
                    Button(
                        onClick = {
                            eventPublisher(MyPortfolioContract.MyPortfolioUIEvent.CloseProfitInfoDialog)
                        }
                    ) {
                        Text("Close")
                    }
                }
            )
        }

        if (state.showTaxInfoDialog) {
            AlertDialog(
                onDismissRequest = {
                    eventPublisher(MyPortfolioContract.MyPortfolioUIEvent.CloseTaxInfoDialog)
                },
                title = { Text("Tax Info", style = MaterialTheme.typography.headlineLarge) },
                text = {
                    Column {
                        Text("Your current tax is:", style = MaterialTheme.typography.bodyLarge, fontSize = 18.sp)
                        Text(tax.toString(), style = MaterialTheme.typography.headlineSmall)
                    }

                       },
                confirmButton = {
                    Button(
                        onClick = {
                            eventPublisher(MyPortfolioContract.MyPortfolioUIEvent.CloseTaxInfoDialog)
                        }
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun SecurityCard(security: SecurityMock) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Security Icon",
                    modifier = Modifier.height(32.dp).width(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = security.ticker,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = security.type,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = security.amount.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Price",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = security.price.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Profit",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = security.profit.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = if (security.profit >= 0) Color.Green else MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Last modified: ${security.lastModified}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Public: ${security.publicCounter}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

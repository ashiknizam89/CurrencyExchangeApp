package com.example.currencyexchangeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.currencyexchangeapp.ui.theme.CurrencyExchangeAppTheme
import com.example.currencyexchangeapp.ui.theme.ExchangeViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.currencyexchangeapp.ui.theme.PrimaryBlue



@Composable
fun AppHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PrimaryBlue,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Currency Exchange",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ExchangeViewModel(application)

        setContent {
            CurrencyExchangeAppTheme {

                val rates by viewModel.rates.collectAsState()
                val from by viewModel.fromCurrency.collectAsState()
                val to by viewModel.toCurrency.collectAsState()
                val amount by viewModel.amount.collectAsState()
                val result by viewModel.convertedValue.collectAsState()

                val currencies = rates.keys.sorted()
                AppHeader()

                // 🔷 MAIN CONTENT BELOW BANNER
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {}
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {

                    // INPUT CARD
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("From")

                            OutlinedTextField(
                                value = amount,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                        viewModel.setAmount(newValue)
                                    }
                                },
                                label = { Text("Amount") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            CurrencyDropdown(
                                selected = from,
                                options = currencies,
                                onSelect = { viewModel.setFromCurrency(it) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SWAP BUTTON
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FloatingActionButton(
                            onClick = { viewModel.swapCurrencies() },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap currencies"
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    // TO CARD
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("To")

                            Text(
                                text = "%.2f".format(result),
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            CurrencyDropdown(
                                selected = to,
                                options = currencies,
                                onSelect = { viewModel.setToCurrency(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyDropdown(
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { expanded = true }) {
        Text("$selected - ${currencyNames[selected] ?: "Unknown"}")
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        options.forEach { currency ->
            DropdownMenuItem(
                text = {
                    Text("$currency - ${currencyNames[currency] ?: "Unknown"}")
                }
                ,
                onClick = {
                    onSelect(currency)
                    expanded = false
                }
            )
        }
    }
}

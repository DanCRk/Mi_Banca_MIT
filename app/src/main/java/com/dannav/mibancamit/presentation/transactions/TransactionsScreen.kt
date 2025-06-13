package com.dannav.mibancamit.presentation.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dannav.mibancamit.ui.theme.ColorText
import com.dannav.mibancamit.presentation.components.cards.NeomorphismCard
import com.dannav.mibancamit.ui.theme.PrimaryLight
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat

@Composable
fun TransactionsScreen(
    modifier: Modifier = Modifier,
    transactionViewModel: TransactionViewModel
) {
    val ui by transactionViewModel.state.collectAsState()

    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "Movimientos",
            color = ColorText,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        when {
            ui.isLoading && ui.elements.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            ui.elements.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No tienes transacciones",
                        modifier = Modifier.align(Alignment.Center),
                        color = ColorText
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(ui.elements.size) {
                        val isDeposit = (ui.elements[it].type == "deposit")

                        val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(Date(ui.elements[it].timestamp))
                        val simbol = if (isDeposit) "+" else "-"
                        val from = if (isDeposit) "Hacia" else "Desde"

                        NeomorphismCard(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = if (isDeposit) "Deposito" else "Pago",
                                    color = ColorText
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = "$from: ${ui.elements[it].fromCardName.chunked(4).joinToString(" ")}",
                                    color = ColorText
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = "Monto: $simbol ${ui.elements[it].amount} MXN",
                                    color = ColorText
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = "Fecha: $formattedDate",
                                    color = ColorText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

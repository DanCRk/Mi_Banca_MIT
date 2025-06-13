package com.dannav.mibancamit.presentation.cards

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dannav.mibancamit.R
import com.dannav.mibancamit.data.model.Card
import com.dannav.mibancamit.presentation.components.buttons.NeomorphismButton
import com.dannav.mibancamit.presentation.components.edittext.NeoEditText
import com.dannav.mibancamit.presentation.transactions.TransactionViewModel
import com.dannav.mibancamit.presentation.transactions.TransactionsScreen
import com.dannav.mibancamit.ui.theme.BackgroundColor
import com.dannav.mibancamit.ui.theme.ColorText
import com.dannav.mibancamit.utils.CardUtils

@Composable
fun CardsScreen(
    modifier: Modifier = Modifier,
    cardsViewModel: MyCardsViewModel,
    transactionViewModel: TransactionViewModel
) {
    val ui by cardsViewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Lanza un efecto para mostrar el Snackbar cuando cambien los mensajes de error o éxito.
    LaunchedEffect(ui.error, ui.success) {
        ui.error?.let {
            snackbarHostState.showSnackbar(it)
            cardsViewModel.clearMessages()
        }
        ui.success?.let {
            snackbarHostState.showSnackbar(it)
            cardsViewModel.clearMessages()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(top = 20.dp)
                .padding(paddingValues)
        ) {
            when {
                ui.isLoading && ui.elements.isEmpty() -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                ui.elements.isEmpty() -> {
                    Text(
                        "No tienes tarjetas",
                        modifier = Modifier.align(Alignment.Center),
                        color = ColorText
                    )
                }

                else -> {
                    Column(Modifier.fillMaxSize()) {
                        CardsSection(ui.elements)
                        TransactionsScreen(transactionViewModel = transactionViewModel)
                    }
                }
            }

            AddCardFab(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 60.dp, end = 20.dp)
                    .size(65.dp)
            ) { holder, number, expiry ->
                Log.i("CardsScreen", "AddCardFab: $holder $number $expiry")
                cardsViewModel.onAddCard(holder, number, expiry)
            }
        }
    }
}


@Composable
fun AddCardFab(
    modifier: Modifier = Modifier,
    onAdd: (name: String, number: String, expiry: String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    NeomorphismButton(
        modifier = modifier,
        text = "+",
        fontSize = 35,
        onClick = { showDialog = true }
    )

    if (showDialog) {
        AddCardDialog(
            onConfirm = { name, number, expiry ->
                onAdd(name, number, expiry.chunked(2).joinToString("/"))
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}



@Composable
fun CardsSection(cards: List<Card>) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
        items(cards.size) {
            CardItem(card = cards[it], modifier = Modifier.padding(end = 16.dp))
        }
    }
}

@Composable
fun CardItem(
    card: Card,
    modifier: Modifier = Modifier
) {
    val logo = when (card.cardType.uppercase()) {
        "MASTERCARD" -> painterResource(R.drawable.ic_mastercard)
        else -> painterResource(R.drawable.ic_visa)
    }

    val formattedBalance = remember(card.balance) {
        "%,.2f".format(card.balance)       // 1 000 000.50 → "1,000,000.50"
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(25.dp))
            .background(card.color)
            .width(250.dp)
            .height(160.dp)
            .clickable { /* Navegar a detalle si quieres */ }
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(logo, contentDescription = card.cardType, modifier = Modifier.width(60.dp))

        Text(
            card.cardName,
            color = ColorText,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "$ $formattedBalance",
            color = ColorText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            card.cardNumber.chunked(4).joinToString(" "),
            color = ColorText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}




@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }

    AlertDialog(
        containerColor = BackgroundColor,
        titleContentColor = ColorText,
        onDismissRequest = onDismiss,
        confirmButton = {
            NeomorphismButton(text = "Agregar") { onConfirm(name, number, expiry) }
        },
        dismissButton = {
            NeomorphismButton(text = "Cancelar") { onDismiss() }

        },
        title = { Text("Nueva tarjeta") },
        text = {
            Column {
                NeoEditText(
                    placeholder = "Nombre",
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    onvalueChange = { name = it },
                    value = name
                )
                Spacer(Modifier.height(16.dp))
                NeoEditText(
                    value = number,
                    onvalueChange = { input -> number = input.filter { it.isDigit() }.take(16) },
                    placeholder = "Número",
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                    visualTransformation = CardUtils.CardVisualTransformation
                )

                Spacer(Modifier.height(16.dp))
                NeoEditText(
                    placeholder = "MM/YY",
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                    value = expiry,
                    onvalueChange = { input ->
                        expiry = input.filter { it.isDigit() }.take(4)
                    },
                    visualTransformation = CardUtils.ExpiryVisualTransformation
                ){
                    onConfirm(name, number, expiry)
                }


            }
        }
    )
}

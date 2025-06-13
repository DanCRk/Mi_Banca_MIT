package com.dannav.mibancamit.presentation.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dannav.mibancamit.data.model.Card
import com.dannav.mibancamit.presentation.cards.MyCardsViewModel
import com.dannav.mibancamit.presentation.components.buttons.NeomorphismButton
import com.dannav.mibancamit.presentation.components.dropdown.NeumorphicDropdown
import com.dannav.mibancamit.presentation.components.edittext.NeoEditText
import com.dannav.mibancamit.ui.theme.BackgroundColor
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect

@Composable
fun PaymentScreen(
    modifier: Modifier = Modifier,
    paymentsViewModel: PaymentViewModel,
    cardsViewModel: MyCardsViewModel,
) {
    val cardsUi by cardsViewModel.state.collectAsState()
    val paymentUi by paymentsViewModel.state.collectAsState()

    var selectedCard by remember { mutableStateOf<Card?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var destinationCardNumber by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }

    val amount = amountText.toDoubleOrNull() ?: 0.0

    val canPay = selectedCard != null &&
            destinationCardNumber.isNotBlank() &&
            amount > 0 &&
            (amount <= (selectedCard?.balance ?: 0.0))

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(paymentUi.error, paymentUi.success) {
        paymentUi.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            paymentsViewModel.clearMessages()
        }
        paymentUi.success?.let { message ->
            snackbarHostState.showSnackbar(message)
            paymentsViewModel.clearMessages()
        }
    }

    LaunchedEffect(cardsUi.elements.isEmpty()) {
        if (cardsUi.elements.isEmpty()) {
            snackbarHostState.showSnackbar("No tienes tarjetas configuradas")
        }
    }

    LaunchedEffect(selectedCard, amount) {
        if (selectedCard != null && amount > selectedCard!!.balance) {
            snackbarHostState.showSnackbar("Saldo insuficiente en la tarjeta seleccionada")
        }
    }

    LaunchedEffect(paymentUi.success) {
        if (paymentUi.success != null) {
            destinationCardNumber = ""
            amountText = ""
            selectedCard = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                if (cardsUi.elements.isNotEmpty()) {
                    NeumorphicDropdown(
                        cards = cardsUi.elements,
                        selectedCard = selectedCard,
                        dropdownExpanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it },
                        onCardSelected = { selectedCard = it }
                    )
                }

                NeoEditText(
                    value = destinationCardNumber,
                    onvalueChange = { destinationCardNumber = it },
                    placeholder = "Número de tarjeta destino",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )

                NeoEditText(
                    value = amountText,
                    onvalueChange = { amountText = it },
                    placeholder = "Monto a pagar",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )

                NeomorphismButton(
                    onClick = {
                        paymentsViewModel.onMakePayment(
                            destinationCardNumber,
                            selectedCard!!.cardId,
                            amount,
                            selectedCard!!.cardNumber
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canPay && !paymentUi.isLoading,
                    text = "Pagar"
                )
            }
        }
    )
}


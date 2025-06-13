package com.dannav.mibancamit.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dannav.mibancamit.presentation.navigationdrawer.model.CustomDrawerState
import com.dannav.mibancamit.presentation.navigationdrawer.model.opposite
import com.dannav.mibancamit.presentation.cards.CardsScreen
import com.dannav.mibancamit.presentation.cards.MyCardsViewModel
import com.dannav.mibancamit.presentation.navigationdrawer.model.NavigationItem
import com.dannav.mibancamit.presentation.payments.PaymentScreen
import com.dannav.mibancamit.presentation.payments.PaymentViewModel
import com.dannav.mibancamit.presentation.transactions.TransactionViewModel
import com.dannav.mibancamit.ui.theme.BackgroundColor
import com.dannav.mibancamit.ui.theme.ColorText
import com.dannav.mibancamit.presentation.transactions.TransactionsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    drawerState: CustomDrawerState,
    onDrawerClick: (CustomDrawerState) -> Unit,
    selectedNavigationItem: NavigationItem,
    onLogOutClick : () -> Unit,
    cardsViewModel: MyCardsViewModel = hiltViewModel(),
    paymentsViewModel: PaymentViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel = hiltViewModel()

) {
    val title by remember { mutableStateOf("Mis Tarjetas") }

    Scaffold(modifier = modifier.clickable(enabled = drawerState == CustomDrawerState.Opened) {
        onDrawerClick(CustomDrawerState.Closed)
    }, topBar = {
        TopAppBar(title = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 55.dp)
                    .background(BackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                when (selectedNavigationItem) {
                    NavigationItem.Home -> {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    else -> {
                        Text(
                            text = selectedNavigationItem.title,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }, navigationIcon = {
            IconButton(onClick = { onDrawerClick(drawerState.opposite()) }) {
                Icon(
                    imageVector = Icons.Default.Menu, contentDescription = "Menu Icon"
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundColor,
            titleContentColor = ColorText,
            navigationIconContentColor = ColorText
        )
        )
    }) { paddingValues ->


        when (selectedNavigationItem){
            NavigationItem.Home -> CardsScreen(Modifier.padding(paddingValues), cardsViewModel, transactionViewModel)
            NavigationItem.Pay -> PaymentScreen (Modifier.padding(paddingValues), paymentsViewModel, cardsViewModel)
            NavigationItem.LogOut -> onLogOutClick()
        }

    }


}

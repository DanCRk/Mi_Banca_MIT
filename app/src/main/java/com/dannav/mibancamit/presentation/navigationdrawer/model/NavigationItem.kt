package com.dannav.mibancamit.presentation.navigationdrawer.model

import com.dannav.mibancamit.R

enum class NavigationItem(
    val title:String,
    val icon:Int
) {
    Home(
        icon = R.drawable.card,
        title = "Mis tarjetas"
    ),
    Pay(
        icon = R.drawable.payments,
        title = "Pagar"
    ),
    LogOut(
        icon = R.drawable.logout,
        title = "Cerrar Sesion"
    )
}
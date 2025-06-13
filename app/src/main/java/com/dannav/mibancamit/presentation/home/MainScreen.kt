package com.dannav.mibancamit.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.dannav.mibancamit.presentation.navigationdrawer.component.CustomDrawer
import com.dannav.mibancamit.presentation.navigationdrawer.model.CustomDrawerState
import com.dannav.mibancamit.presentation.navigationdrawer.model.isOpened
import com.dannav.mibancamit.presentation.navigationdrawer.model.NavigationItem
import com.dannav.mibancamit.ui.theme.BackgroundColor
import com.dannav.mibancamit.ui.theme.PrimaryLight
import com.dannav.mibancamit.utils.coloredShadow
import kotlin.math.roundToInt


@Composable
fun MainScreen(
    onLogout: (Int) -> Unit,
) {
    var drawerState by remember { mutableStateOf(CustomDrawerState.Closed) }
    var selectedNavigationItem by remember { mutableStateOf(NavigationItem.Home) }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current.density

    val screenWidth = remember {
        derivedStateOf { (configuration.screenWidthDp * density).roundToInt() }
    }
    val offsetValue by remember { derivedStateOf { (screenWidth.value / 4.5).dp } }
    val animatedOffset by animateDpAsState(
        targetValue = if (drawerState.isOpened()) offsetValue else 0.dp, label = "Animated Offset"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (drawerState.isOpened()) 0.9f else 1f, label = "Animated Scale"
    )



    BackHandler(enabled = drawerState.isOpened()) {
        drawerState = CustomDrawerState.Closed
    }

    Box(
        modifier = Modifier
            .background(BackgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        CustomDrawer(selectedNavigationItem = selectedNavigationItem, onNavigationItemClick = {
            selectedNavigationItem = it
            drawerState = CustomDrawerState.Closed
        }, onCloseClick = { drawerState = CustomDrawerState.Closed })

        HomeContent(
            modifier = Modifier
                .offset(x = animatedOffset)
                .scale(scale = animatedScale)
                .coloredShadow(
                    color = Color.Black, alpha = 0.1f, shadowRadius = 50.dp
                ),
            drawerState = drawerState,
            onDrawerClick = { drawerState = it },
            selectedNavigationItem = selectedNavigationItem,
        )
    }
}
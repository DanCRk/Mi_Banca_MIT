package com.dannav.mibancamit.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dannav.mibancamit.R
import com.dannav.mibancamit.presentation.components.buttons.NeomorphismButton
import com.dannav.mibancamit.presentation.components.cards.NeomorphismCard
import com.dannav.mibancamit.presentation.components.edittext.NeoEditText
import com.dannav.mibancamit.presentation.loading.FullScreenProgressBar
import com.dannav.mibancamit.ui.theme.BackgroundColor
import com.dannav.mibancamit.ui.theme.ColorText

@Composable
fun Login(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(),
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val email by loginViewModel.email.collectAsStateWithLifecycle()
    val password by loginViewModel.password.collectAsStateWithLifecycle()

    val loginEnable by loginViewModel.loginEnable.collectAsStateWithLifecycle()

    val isLogin by loginViewModel.isLogin.collectAsStateWithLifecycle()
    val loginSuccess by loginViewModel.loginSuccess.collectAsStateWithLifecycle()
    val loginSuccessMessage by loginViewModel.loginSuccessMessage.collectAsStateWithLifecycle()

    if (isLogin) {
        FullScreenProgressBar(text = "Iniciando Sesion")
    }

    if (loginSuccess) {
        onLoginSuccess()
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .padding(top = 80.dp)
        ) {

            NeomorphismCard(
                modifier = Modifier
                    .padding(top = 80.dp)
                    .align(Alignment.CenterHorizontally)
                    .width(130.dp)
                    .height(130.dp)
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bank))
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                )
            }
            Text(
                text = "KazeMessage",
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 30.sp,
                color = ColorText,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Login",
                modifier = Modifier
                    .padding(top = 15.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
                color = ColorText,
                textAlign = TextAlign.Center
            )



            NeoEditText(
                placeholder = "Email o Usuario",
                keyboardType = KeyboardType.Email,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                value = email,
                imeAction = ImeAction.Next,
                icon = Icons.Filled.Email,
                onvalueChange = { loginViewModel.onLoginChange(it, password) }
            )

            NeoEditText(
                placeholder = "Contraseña",
                keyboardType = KeyboardType.Email,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                value = password,
                imeAction = ImeAction.Done,
                isPassword = true,
                icon = Icons.Filled.Lock,
                onvalueChange = { loginViewModel.onLoginChange(email, it) }
            ) {
                if (loginEnable) loginViewModel.onLoginClick()

            }

            Text(
                text = "Aun no tienes cuenta? Registrate",
                modifier = Modifier
                    .padding(top = 25.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { onRegisterClick() },
                fontSize = 16.sp,
                color = ColorText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            NeomorphismButton(
                enabled = loginEnable,
                text = "LOGIN",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 80.dp),
                textStyle = TextStyle(
                    fontSize = 22.sp,
                    letterSpacing = 0.2.em, // Espaciado entre letras
                    lineHeight = 24.sp, // Altura de la línea
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            ) {
                loginViewModel.onLoginClick()
            }

            LaunchedEffect(loginSuccessMessage) {
                if (loginSuccessMessage.isNotEmpty()) {
                    snackbarHostState.showSnackbar(
                        message = loginSuccessMessage,
                        duration = SnackbarDuration.Short
                    )
                    loginViewModel.clearMessage()
                }
            }

        }
    }
}
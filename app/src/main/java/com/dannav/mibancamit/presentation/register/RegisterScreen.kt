package com.dannav.mibancamit.presentation.register

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
import androidx.compose.material.icons.filled.Person
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dannav.mibancamit.R
import com.dannav.mibancamit.data.Resource
import com.dannav.mibancamit.presentation.components.buttons.NeomorphismButton
import com.dannav.mibancamit.presentation.components.cards.NeomorphismCard
import com.dannav.mibancamit.presentation.components.edittext.NeoEditText
import com.dannav.mibancamit.presentation.loading.FullScreenProgressBar
import com.dannav.mibancamit.ui.theme.BackgroundColor
import com.dannav.mibancamit.ui.theme.ColorText

@Composable
fun Register(
    registerViewModel: RegisterViewModel = hiltViewModel(),
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {

    val email by registerViewModel.email.collectAsStateWithLifecycle()
    val user by registerViewModel.user.collectAsStateWithLifecycle()
    val password by registerViewModel.password.collectAsStateWithLifecycle()
    val repeatPassword by registerViewModel.repeatPassword.collectAsStateWithLifecycle()

    val registerEnable by registerViewModel.registerEnable.collectAsStateWithLifecycle()

    val state by registerViewModel.uiState.collectAsStateWithLifecycle()
    val registerSuccessMessage by registerViewModel.registerSuccessMessage.collectAsStateWithLifecycle()

    if (state is Resource.Loading) {
        FullScreenProgressBar(text = "Registrando Usuario")
    }

    if (state is Resource.Success) {
        onRegisterSuccess()
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
                .padding(top = 30.dp)
        ) {

            NeomorphismCard(
                modifier = Modifier
                    .padding(top = 50.dp)
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
                text = "Mi Banca MIT",
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 30.sp,
                color = ColorText,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Registro",
                modifier = Modifier
                    .padding(top = 15.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
                color = ColorText,
                textAlign = TextAlign.Center
            )

            NeoEditText(
                placeholder = "Usuario",
                keyboardType = KeyboardType.Text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                value = user,
                imeAction = ImeAction.Next,
                icon = Icons.Filled.Person,
                onvalueChange = {
                    registerViewModel.onRegisterChange(
                        it,
                        email,
                        password,
                        repeatPassword
                    )
                }
            )

            NeoEditText(
                placeholder = "Email",
                keyboardType = KeyboardType.Email,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                value = email,
                imeAction = ImeAction.Next,
                icon = Icons.Filled.Email,
                onvalueChange = {
                    registerViewModel.onRegisterChange(
                        user,
                        it,
                        password,
                        repeatPassword
                    )
                }
            )

            NeoEditText(
                placeholder = "Contraseña",
                keyboardType = KeyboardType.Password,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                value = password,
                imeAction = ImeAction.Next,
                isPassword = true,
                icon = Icons.Filled.Lock,
                onvalueChange = {
                    registerViewModel.onRegisterChange(
                        user,
                        email,
                        it,
                        repeatPassword
                    )
                }
            )

            NeoEditText(
                placeholder = "Repite Contraseña",
                keyboardType = KeyboardType.Password,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                value = repeatPassword,
                imeAction = ImeAction.Done,
                isPassword = true,
                icon = Icons.Filled.Lock,
                onvalueChange = { registerViewModel.onRegisterChange(user, email, password, it) }
            ) {
                if (registerEnable) registerViewModel.onRegisterClick()
            }

            Text(
                text = "Ya tienes cuenta? Inicia Sesion",
                modifier = Modifier
                    .padding(top = 25.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { onLoginClick() },
                fontSize = 16.sp,
                color = ColorText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            NeomorphismButton(
                enabled = registerEnable,
                text = "REGISTRO",
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
                registerViewModel.onRegisterClick()
            }

            if(state is Resource.Failure ) {
                LaunchedEffect(key1 = 0) {
                    snackbarHostState.showSnackbar(
                        message =( state as Resource.Failure).message,
                        duration = SnackbarDuration.Short
                    )
                    registerViewModel.clearMessage()
                }

            }

        }
    }
}
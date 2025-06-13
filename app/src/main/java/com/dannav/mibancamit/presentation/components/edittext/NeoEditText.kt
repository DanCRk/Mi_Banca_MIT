package com.dannav.mibancamit.presentation.components.edittext

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.dannav.mibancamit.R
import com.dannav.mibancamit.ui.theme.BackgroundButtonDarkShadow
import com.dannav.mibancamit.ui.theme.BackgroundButtonLightShadow
import com.dannav.mibancamit.ui.theme.BackgroundColor
import com.dannav.mibancamit.ui.theme.ColorPlaceHolder
import com.dannav.mibancamit.ui.theme.ColorText
import com.gandiva.neumorphic.LightSource
import com.gandiva.neumorphic.neu
import com.gandiva.neumorphic.shape.Flat
import com.gandiva.neumorphic.shape.Pressed
import com.gandiva.neumorphic.shape.RoundedCorner

@Composable
fun NeoEditText(
    modifier: Modifier = Modifier, placeholder: String, keyboardType: KeyboardType,
    singleLine: Boolean = false,
    maxLines: Int = 1,
    value: String = "",
    icon: ImageVector,
    isPassword:Boolean = false,
    imeAction: ImeAction,
    onvalueChange: (String) -> Unit,
    onSendAction: () -> Unit = {}
) {

    var isFocused by remember {
        mutableStateOf(false)
    }
    val focusRequester = remember {
        FocusRequester()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        value = value,
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = "", tint = ColorText)
        },
        visualTransformation = if (passwordVisible || !isPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            if (isPassword){
                val image = if (passwordVisible) R.drawable.ic_visible else R.drawable.ic_no_visible
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick =
                { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(id = image),
                        contentDescription = description,
                        tint = ColorText
                    )
                }
            }
        },
        onValueChange = { onvalueChange(it) },
        modifier = modifier
            .neu(
                lightShadowColor = BackgroundButtonLightShadow,
                darkShadowColor = BackgroundButtonDarkShadow,
                shadowElevation = 6.dp,
                lightSource = LightSource.LEFT_TOP,
                shape = if (isFocused) Pressed(RoundedCorner(17.dp)) else Flat(RoundedCorner(17.dp)),
            )
            .onFocusEvent {
                isFocused = it.isFocused
            }
            .focusRequester(focusRequester),
        placeholder = { Text(text = placeholder) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusRequester.freeFocus()
                onSendAction()

            },
        ),
        singleLine = singleLine,
        maxLines = maxLines,
        colors = TextFieldDefaults.colors(
            unfocusedTextColor = ColorPlaceHolder,
            focusedTextColor = ColorText,
            focusedPlaceholderColor = ColorPlaceHolder,
            unfocusedPlaceholderColor = ColorPlaceHolder,
            disabledContainerColor = BackgroundColor,
            focusedContainerColor = BackgroundColor,
            unfocusedContainerColor = BackgroundColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = ColorText
        ),
        shape = RoundedCornerShape(17.dp)
    )
}
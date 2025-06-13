package com.dannav.mibancamit.presentation.components.dropdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dannav.mibancamit.data.model.Card
import com.dannav.mibancamit.ui.theme.BackgroundColor
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.foundation.layout.fillMaxWidth
import com.dannav.mibancamit.ui.theme.BackgroundButtonDarkShadow
import com.dannav.mibancamit.ui.theme.BackgroundButtonLightShadow
import com.gandiva.neumorphic.LightSource
import com.gandiva.neumorphic.neu
import com.gandiva.neumorphic.shape.Flat
import com.gandiva.neumorphic.shape.Pressed
import com.gandiva.neumorphic.shape.RoundedCorner
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import com.dannav.mibancamit.ui.theme.ColorPlaceHolder
import com.dannav.mibancamit.ui.theme.ColorText
import com.dannav.mibancamit.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeumorphicDropdown(
    modifier: Modifier = Modifier,
    cards: List<Card>,
    selectedCard: Card?,
    dropdownExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCardSelected: (Card) -> Unit
) {
    val containerColor = BackgroundColor

    ExposedDropdownMenuBox(
        expanded = dropdownExpanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
            .fillMaxWidth()
            .neu(
                lightShadowColor = BackgroundButtonLightShadow,
                darkShadowColor = BackgroundButtonDarkShadow,
                shadowElevation = 6.dp,
                lightSource = LightSource.LEFT_TOP,
                shape = if (dropdownExpanded) Pressed(RoundedCorner(17.dp))
                else Flat(RoundedCorner(17.dp))
            )
            // Aquí se fija el fondo
            .background(containerColor)
    ) {
        // TextField de selección: se le asignan los colores para que use nuestro BackgroundColor
        TextField(
            value = selectedCard?.cardName ?: "Selecciona una tarjeta",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = { Text("Tarjeta Origen") },
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = ColorText,
                focusedTextColor = ColorText,
                focusedPlaceholderColor = ColorText,
                unfocusedPlaceholderColor = ColorText,
                disabledContainerColor = BackgroundColor,
                focusedContainerColor = BackgroundColor,
                unfocusedContainerColor = BackgroundColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = ColorText,
                unfocusedLabelColor = ColorText,
                focusedLabelColor = ColorText,
                errorLabelColor = Primary,
                disabledLabelColor = ColorText
            )
        )
        // Menú desplegable: le agregamos la neumorfosis y el fondo
        ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .fillMaxWidth()
                .neu(
                    lightShadowColor = BackgroundButtonLightShadow,
                    darkShadowColor = BackgroundButtonDarkShadow,
                    shadowElevation = 6.dp,
                    lightSource = LightSource.LEFT_TOP,
                    shape = if (dropdownExpanded) Pressed(RoundedCorner(17.dp))
                    else Flat(RoundedCorner(17.dp))
                )
                .background(containerColor)
        ) {
            cards.forEach { card ->
                Spacer(Modifier.height(8.dp))
                DropdownMenuItem(
                    onClick = {
                        onCardSelected(card)
                        onExpandedChange(false)
                    },
                    modifier = Modifier.neu(
                        lightShadowColor = BackgroundButtonLightShadow,
                        darkShadowColor = BackgroundButtonDarkShadow,
                        shadowElevation = 6.dp,
                        lightSource = LightSource.LEFT_TOP,
                        shape =  Pressed(RoundedCorner(17.dp))
                    ).menuAnchor(),
                    text = {
                        Text(text = "${card.cardName} - Saldo: ${card.balance}", color = ColorText)
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

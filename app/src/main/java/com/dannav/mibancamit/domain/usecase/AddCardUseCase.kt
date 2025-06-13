package com.dannav.mibancamit.domain.usecase

import android.util.Log
import com.dannav.mibancamit.data.Resource
import com.dannav.mibancamit.data.model.Card
import com.dannav.mibancamit.data.remote.DatabaseRepository
import com.dannav.mibancamit.utils.CardUtils
import com.dannav.mibancamit.utils.CardUtils.detectCardType
import com.dannav.mibancamit.utils.ColorUtils.getCardColor
import javax.inject.Inject

class AddCardUseCase @Inject constructor(
    private val repo: DatabaseRepository
) {
    suspend operator fun invoke(
        holderName: String,
        number: String,
        expiry: String
    ): Resource<Unit> {

        if (!CardUtils.nameOk(holderName))
            return Resource.Failure(
                IllegalArgumentException(),
                "Nombre inválido (mín. 3 letras)."
            )

        if (!CardUtils.numberOk(number))
            return Resource.Failure(
                IllegalArgumentException(),
                "Número de tarjeta inválido."
            )

        if (!CardUtils.expiryOk(expiry))
            return Resource.Failure(
                IllegalArgumentException(),
                "Fecha inválida; usa MM/YY."
            )

        val brand = detectCardType(number)
        val card  = Card(
            cardType    = brand,
            cardNumber  = number,
            cardName    = holderName,
            balance     = 10000.0,
            color       = getCardColor(brand)
        )
        Log.i("DatabaseRepository", "Adding card: $card")

        return try {
            repo.addCard(card)
            Resource.Success(Unit, "Tarjeta añadida 🎉")
        } catch (e: Exception) {
            Resource.Failure(e, "No se pudo registrar.")
        }
    }
}
package com.dannav.mibancamit.domain.usecase

import com.dannav.mibancamit.data.Resource
import com.dannav.mibancamit.data.remote.DatabaseRepository
import javax.inject.Inject
import android.util.Log


class PaymentUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {
    suspend operator fun invoke(
        destinationCardNumber: String,
        fromCardId: String,
        fromCardName: String,
        amount: Double
    ): Resource<Unit> {
        Log.i("PaymentUseCase", "Realizando pago de $amount desde $fromCardId ($fromCardName) a $destinationCardNumber")

        val recipient = databaseRepository.findRecipientByCardNumber(destinationCardNumber)
            ?: return Resource.Failure(Exception(), "Destino no encontrado")

        val (receiverUid, toCardId) = recipient

        return try {
            databaseRepository.pay(receiverUid, fromCardId, toCardId, amount, fromCardName)
            Resource.Success(Unit, "Pago realizado exitosamente")
        } catch(e: Exception) {
            Resource.Failure(Exception(), "Error al realizar el pago: ${e.message}")
        }
    }
}

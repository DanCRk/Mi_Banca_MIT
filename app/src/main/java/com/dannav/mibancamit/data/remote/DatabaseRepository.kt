package com.dannav.mibancamit.data.remote

import android.util.Log
import com.dannav.mibancamit.data.model.Card
import com.dannav.mibancamit.utils.ColorUtils.getCardColor
import com.dannav.mibancamit.utils.CryptoUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val database: FirebaseDatabase
) {

    suspend fun addCard(card: Card) {
        Log.i("DatabaseRepository", "Adding card: $card")
        val uid = authRepository.currentUser!!.uid
        // Se genera la referencia con push() para obtener el identificador único (cardId)
        val cardRef = database.getReference("users/$uid/cards").push()
        val cardId = cardRef.key ?: throw IllegalStateException("Card key not generated")

        // 🔐 Ciframos los campos sensibles
        val encType   = CryptoUtils.encrypt(card.cardType)
        val encNumber = CryptoUtils.encrypt(card.cardNumber)
        val encName   = CryptoUtils.encrypt(card.cardName)

        val data = mapOf(
            "cardId"      to cardId, // Campo en claro para identificar la tarjeta
            "cardType"    to encType.cipherText,
            "cardType_iv" to encType.iv,
            "cardNumber"  to encNumber.cipherText,
            "cardNumber_iv" to encNumber.iv,
            "cardName"    to encName.cipherText,
            "cardName_iv" to encName.iv,
            "balance"     to card.balance,
            "addedAt"     to ServerValue.TIMESTAMP
        )

        cardRef.setValue(data).await()
    }


    suspend fun pay(toUid: String, fromCardId: String, toCardId: String, amount: Double) {
        val senderUid = authRepository.currentUser!!.uid
        val root = database.reference

        val updates = mapOf(
            // Se debita el balance en la tarjeta del remitente
            "/users/$senderUid/cards/$fromCardId/balance" to ServerValue.increment(-amount),
            // Se acredita el balance en la tarjeta del destinatario
            "/users/$toUid/cards/$toCardId/balance" to ServerValue.increment(+amount)
        )
        root.updateChildren(updates).await()

        recordTransaction(senderUid, toUid, fromCardId, toCardId, amount)
    }




    fun observeCards(): Flow<List<Card>> = callbackFlow {
        val uid = authRepository.currentUser!!.uid
        val ref = database.getReference("users/$uid/cards")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cards = snapshot.children.mapNotNull { snap ->
                    try {
                        val cardId = snap.child("cardId").value as? String ?: snap.key ?: ""
                        val typeEnc = CryptoUtils.Enc(
                            cipherText = snap.child("cardType").value as String,
                            iv         = snap.child("cardType_iv").value as String
                        )
                        val numberEnc = CryptoUtils.Enc(
                            cipherText = snap.child("cardNumber").value as String,
                            iv         = snap.child("cardNumber_iv").value as String
                        )
                        val nameEnc = CryptoUtils.Enc(
                            cipherText = snap.child("cardName").value as String,
                            iv         = snap.child("cardName_iv").value as String
                        )

                        val type     = CryptoUtils.decrypt(typeEnc)
                        val number   = CryptoUtils.decrypt(numberEnc)
                        val name     = CryptoUtils.decrypt(nameEnc)
                        val balance  = (snap.child("balance").value as? Number)?.toDouble() ?: 0.0

                        // Aquí, para el color podrías usar la función getCardColor según el tipo
                        Card(
                            cardId = cardId,
                            cardType = type,
                            cardNumber = number,
                            cardName = name,
                            balance = balance,
                            color = getCardColor(type)
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(cards).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }


    private suspend fun recordTransaction(
        senderUid: String,
        receiverUid: String,
        fromCardId: String,
        toCardId: String,
        amount: Double
    ) {
        val transactionData = mapOf(
            "fromCardId"  to fromCardId,       // Tarjeta del remitente
            "toCardId"    to toCardId,         // Tarjeta del destinatario
            "fromUid"     to senderUid,
            "toUid"       to receiverUid,
            "amount"      to amount,
            "timestamp"   to ServerValue.TIMESTAMP
        )

        // Registra el movimiento en el nodo del remitente
        val senderRef = database.getReference("users/$senderUid/transactions").push()
        senderRef.setValue(transactionData).await()

        // Registra el movimiento en el nodo del destinatario
        val receiverRef = database.getReference("users/$receiverUid/transactions").push()
        receiverRef.setValue(transactionData).await()
    }


    suspend fun findRecipientByCardNumber(cardNumberPlain: String): Pair<String, String>? {
        val usersRef = database.getReference("users")
        val snapshot = usersRef.get().await()
        snapshot.children.forEach { userSnap ->
            val userUid = userSnap.key ?: return@forEach
            val cardsSnap = userSnap.child("cards")
            cardsSnap.children.forEach { cardSnap ->
                try {
                    val encNumber = CryptoUtils.Enc(
                        cipherText = cardSnap.child("cardNumber").value as String,
                        iv = cardSnap.child("cardNumber_iv").value as String
                    )
                    // Se desencripta y se compara
                    if (CryptoUtils.decrypt(encNumber) == cardNumberPlain) {
                        val cardId = cardSnap.key ?: ""
                        return Pair(userUid, cardId)
                    }
                } catch (e: Exception) {
                }
            }
        }
        return null
    }


}

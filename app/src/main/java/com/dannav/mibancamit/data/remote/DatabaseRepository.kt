package com.dannav.mibancamit.data.remote

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
        val uid = authRepository.currentUser!!.uid
        val cardRef = database.getReference("users/$uid/cards").push()

        // 🔐 Ciframos los campos sensibles
        val encType   = CryptoUtils.encrypt(card.cardType)
        val encNumber = CryptoUtils.encrypt(card.cardNumber)
        val encName   = CryptoUtils.encrypt(card.cardName)

        val data = mapOf(
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

    suspend fun topUp(cardId: String, amountCents: Long) {
        val uid = authRepository.currentUser!!.uid

        val balRef = database
            .getReference("users/$uid/cards/$cardId/balance")

        balRef.setValue(ServerValue.increment(amountCents)).await()
    }

    suspend fun pay(toUid: String, fromCard: String, amount: Long) {
        val uid = authRepository.currentUser!!.uid

        val root = database.reference

        val updates = mapOf(
            "/users/$uid/cards/$fromCard/balance"      to ServerValue.increment(-amount),
            "/users/$toUid/cards/$fromCard/balance"    to ServerValue.increment(+amount)
        )
        root.updateChildren(updates).await()
    }

    fun observeCards(): Flow<List<Card>> = callbackFlow {
        val uid = authRepository.currentUser!!.uid
        val ref = database.getReference("users/$uid/cards")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cards = snapshot.children.mapNotNull { snap ->
                    try {
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

                        Card(
                            cardType   = type,
                            cardNumber = number,
                            cardName   = name,
                            balance    = balance,
                            color      = getCardColor(type)
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
}

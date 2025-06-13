package com.dannav.mibancamit.data.remote

import android.util.Log
import com.dannav.mibancamit.data.model.Card
import com.dannav.mibancamit.data.model.Transaction
import com.dannav.mibancamit.utils.ColorUtils.getCardColor
import com.dannav.mibancamit.utils.CryptoUtils
import com.dannav.mibancamit.utils.CryptoUtils.sha256Base64
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

    class DuplicateCardException : Exception("La tarjeta ya existe")

    suspend fun addCard(card: Card) {
        val uid = authRepository.currentUser!!.uid
        val cardsRef = database.getReference("users/$uid/cards")

        val numberHash = sha256Base64(card.cardNumber)

        val dupSnap = cardsRef
            .orderByChild("cardNumber_hash")
            .equalTo(numberHash)
            .get()
            .await()

        if (dupSnap.exists()) throw DuplicateCardException()

        val cardRef = cardsRef.push()
        val cardId  = cardRef.key ?: error("Key null")

        val encType   = CryptoUtils.encrypt(card.cardType)
        val encNumber = CryptoUtils.encrypt(card.cardNumber)
        val encName   = CryptoUtils.encrypt(card.cardName)

        val data = mapOf(
            "cardId"          to cardId,
            "cardType"        to encType.cipherText,
            "cardType_iv"     to encType.iv,
            "cardNumber"      to encNumber.cipherText,
            "cardNumber_iv"   to encNumber.iv,
            "cardNumber_hash" to numberHash,
            "cardName"        to encName.cipherText,
            "cardName_iv"     to encName.iv,
            "balance"         to card.balance,
            "addedAt"         to ServerValue.TIMESTAMP
        )

        cardRef.setValue(data).await()
    }



    suspend fun pay(
        toUid: String,
        fromCardId: String,
        toCardId: String,
        amount: Double,
        fromCardName: String
    ) {
        val senderUid = authRepository.currentUser!!.uid
        val root = database.reference

        val updates = mapOf(
            "/users/$senderUid/cards/$fromCardId/balance" to ServerValue.increment(-amount),
            "/users/$toUid/cards/$toCardId/balance" to ServerValue.increment(+amount)
        )
        root.updateChildren(updates).await()

        recordTransaction(senderUid, toUid, fromCardId, toCardId, amount, fromCardName)
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
        amount: Double,
        fromCardName: String,
    ) {
        Log.i("DatabaseRepository", "Recording transaction: $senderUid -> $receiverUid, amount: $amount")
        val transactionData = mapOf(
            "fromUid"       to senderUid,
            "toUid"         to receiverUid,
            "fromCardId"    to fromCardId,
            "toCardId"      to toCardId,
            "fromCardName"  to fromCardName,
            "amount"        to amount,
            "timestamp"     to ServerValue.TIMESTAMP,
        )

        if (senderUid == receiverUid) {
            val ref = database.getReference("users/$senderUid/transactions").push()
            ref.setValue(transactionData).await()
        } else {
            val senderRef = database.getReference("users/$senderUid/transactions").push()
            senderRef.setValue(transactionData).await()

            val receiverRef = database.getReference("users/$receiverUid/transactions").push()
            receiverRef.setValue(transactionData).await()
        }
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

    fun observeTransactions(): Flow<List<Transaction>> = callbackFlow {
        val currentUid = authRepository.currentUser!!.uid
        val ref = database.getReference("users/$currentUid/transactions")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions = snapshot.children.mapNotNull { snap ->
                    try {
                        val fromUid = snap.child("fromUid").value as? String ?: ""
                        val toUid = snap.child("toUid").value as? String ?: ""
                        val fromCardId = snap.child("fromCardId").value as? String ?: ""
                        val toCardId = snap.child("toCardId").value as? String ?: ""
                        val fromCardName = snap.child("fromCardName").value as? String ?: ""
                        val amount = (snap.child("amount").value as? Number)?.toDouble() ?: 0.0
                        val timestamp = when (val timestampAny = snap.child("timestamp").value) {
                            is Long -> timestampAny
                            is Double -> timestampAny.toLong()
                            else -> 0L
                        }
                        val type = if (toUid == currentUid) "deposit" else "pay"

                        Transaction(
                            fromUid = fromUid,
                            toUid = toUid,
                            fromCardId = fromCardId,
                            toCardId = toCardId,
                            fromCardName = fromCardName,
                            amount = amount,
                            timestamp = timestamp,
                            type = type
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(transactions).isSuccess
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }





}

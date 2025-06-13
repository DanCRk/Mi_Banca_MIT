package com.dannav.mibancamit.data.remote

import com.dannav.mibancamit.data.Resource
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    private val usernameCol
        get() = firestore.collection("usernameIndex")

    private fun normalize(raw: String) =
        raw.trim().lowercase().replace("\\s+".toRegex(), "_")

    suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!, "Login exitoso")
        } catch (e: FirebaseAuthInvalidUserException) {
            Resource.Failure(e, "El usuario no existe o ha sido deshabilitado.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Resource.Failure(
                e,
                "Credenciales inválidas. Por favor, verifica tu email y contraseña."
            )
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Failure(e, "Ya existe un usuario con este email.")
        } catch (e: FirebaseAuthEmailException) {
            Resource.Failure(e, "Hay un problema con el email proporcionado.")
        } catch (e: Exception) {
            Resource.Failure(e, "Error interno, contacte al administrador.")
        }
    }

    suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Resource<FirebaseUser> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .await().user!!.also { user ->
                    user.updateProfile(userProfileChangeRequest { displayName = name }).await()
                }
            Resource.Success(firebaseAuth.currentUser!!, "Cuenta creada con exito")
        } catch (e: FirebaseAuthInvalidUserException) {
            Resource.Failure(e, "El usuario no existe o ha sido deshabilitado.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Resource.Failure(
                e,
                "Credenciales inválidas. Por favor, verifica tu email y contraseña."
            )
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Failure(e, "Ya existe un usuario con este email.")
        } catch (e: FirebaseAuthEmailException) {
            Resource.Failure(e, "Hay un problema con el email proporcionado.")
        } catch (e: Exception) {
            Resource.Failure(e, "Error interno, contacte al administrador.")
        }
    }

    suspend fun reAuthenticate(password: String): Resource<FirebaseUser> {
        return try {
            if (currentUser == null) {
                throw FirebaseAuthException("404", "Inicia sesion primero")
            }
            val credentials =
                EmailAuthProvider.getCredential(currentUser!!.email.toString(), password)
            val response = firebaseAuth.signInWithCredential(credentials).await()
            Resource.Success(response.user!!, "Login exitoso")

        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e, e.message.toString())
        }
    }

    suspend fun logout() {
        firebaseAuth.signOut()
    }

    suspend fun registerUsername(usernameRaw: String, email: String) {
        val username = normalize(usernameRaw)
        val docRef = usernameCol.document(username)

        firestore.runTransaction { tx ->
            val snapshot = tx.get(docRef)
            if (snapshot.exists()) {
                throw UsernameTakenException(username)
            }
            tx.set(
                docRef,
                mapOf(
                    "email" to email,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
        }.await()
    }

    suspend fun getEmailByUsername(usernameRaw: String): String {
        val username = normalize(usernameRaw)
        val doc = usernameCol.document(username).get().await()
        if (!doc.exists()) throw UsernameNotFoundException(username)
        return doc.getString("email")!!
    }

}

class UsernameTakenException(val username: String) : Exception()
class UsernameNotFoundException(val username: String) : Exception()
package com.dannav.mibancamit.domain.usecase

import com.dannav.mibancamit.data.Resource
import com.dannav.mibancamit.data.remote.AuthRepository
import com.dannav.mibancamit.data.remote.UsernameTakenException
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repo: AuthRepository
) {

    suspend operator fun invoke(
        username: String,
        email: String,
        password: String
    ): Resource<FirebaseUser> = try {

        repo.registerUsername(username, email)

        repo.signUp(username, email, password).also { result ->
            if (result is Resource.Failure) {
                repo.unregisterUsername(username)
            }
        }

    } catch (e: UsernameTakenException) {
        Resource.Failure(e, "Nombre de usuario ya en uso.")
    } catch (e: Exception) {
        Resource.Failure(e, e.message ?: "Error interno al registrar.")
    }
}
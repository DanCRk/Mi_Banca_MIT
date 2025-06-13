package com.dannav.mibancamit.domain.usecase

import com.dannav.mibancamit.data.Resource
import com.dannav.mibancamit.data.remote.AuthRepository
import com.dannav.mibancamit.data.remote.UsernameNotFoundException
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repo: AuthRepository
) {

    suspend operator fun invoke(
        identifier: String,
        password: String
    ): Resource<FirebaseUser> = try {

        val email = if (identifier.contains("@")) {
            identifier.trim()
        } else {
            repo.getEmailByUsername(identifier.trim())
        }

        repo.login(email, password)

    } catch (e: UsernameNotFoundException) {
        Resource.Failure(e, "Nombre de usuario no encontrado.")
    } catch (e: Exception) {
        Resource.Failure(e, e.message ?: "Error desconocido")
    }
}
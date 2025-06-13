package com.dannav.mibancamit.domain.usecase

import com.dannav.mibancamit.data.remote.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke():Boolean {
        return authRepository.currentUser != null
    }
}

package com.dannav.mibancamit.domain.usecase

import com.dannav.mibancamit.utils.isValidEmail
import com.dannav.mibancamit.utils.isValidPassword
import com.dannav.mibancamit.utils.isValidUsername
import javax.inject.Inject

class ValidateCredentialsUseCase @Inject constructor(){

    operator fun invoke(identifier: String, password: String): Boolean =
        (identifier.isValidEmail() || identifier.isValidUsername()) &&
                password.isValidPassword()
}
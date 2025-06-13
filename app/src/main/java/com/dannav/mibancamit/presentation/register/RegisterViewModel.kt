package com.dannav.mibancamit.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dannav.mibancamit.data.Resource
import com.dannav.mibancamit.domain.usecase.LoginUseCase
import com.dannav.mibancamit.domain.usecase.RegisterUseCase
import com.dannav.mibancamit.domain.usecase.ValidateCredentialsUseCase
import com.dannav.mibancamit.utils.isValidEmail
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Thread.State
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val validateCredentials: ValidateCredentialsUseCase,
    private val register: RegisterUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val uiState : StateFlow<Resource<FirebaseUser>?> = _uiState

    private val _user = MutableStateFlow<String>("")
    val user: StateFlow<String> = _user

    private val _email = MutableStateFlow<String>("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow<String>("")
    val password: StateFlow<String> = _password

    private val _repeatPassword = MutableStateFlow<String>("")
    val repeatPassword: StateFlow<String> = _repeatPassword

    private val _registerEnable = MutableStateFlow<Boolean>(false)
    val registerEnable: StateFlow<Boolean> = _registerEnable


    fun onRegisterChange(user: String, email: String, password: String, repeatPassword: String) {
        _email.value = email
        _user.value = user
        _password.value = password
        _repeatPassword.value = repeatPassword

        _registerEnable.value = validateCredentials(
            _email.value,
            _password.value
        ) && _password.value == _repeatPassword.value

    }

    fun onRegisterClick() = viewModelScope.launch {

        viewModelScope.launch {
            _uiState.value = Resource.Loading
            delay(1000)
            _uiState.value = register(_user.value, _email.value, _password.value)
        }

    }

}

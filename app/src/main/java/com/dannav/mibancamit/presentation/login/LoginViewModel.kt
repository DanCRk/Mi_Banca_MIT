package com.dannav.mibancamit.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dannav.mibancamit.data.Resource
import com.dannav.mibancamit.domain.usecase.LoginUseCase
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
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validateCredentials: ValidateCredentialsUseCase,
    private val login: LoginUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val uiState = _uiState.asStateFlow()

    private val _email = MutableStateFlow<String>("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow<String>("")
    val password: StateFlow<String> = _password

    private val _loginEnable = MutableStateFlow<Boolean>(false)
    val loginEnable: StateFlow<Boolean> = _loginEnable


    fun onLoginChange(email: String, password: String) {
        _email.value = email
        _password.value = password

        _loginEnable.value = validateCredentials(_email.value, _password.value)

    }

    fun onLoginClick() = viewModelScope.launch {

        viewModelScope.launch {
            _uiState.value = Resource.Loading
            delay(1000)
            _uiState.value = login(_email.value, _password.value)
        }

    }

}

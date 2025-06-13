package com.dannav.mibancamit.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dannav.mibancamit.data.Resource
import com.dannav.mibancamit.domain.usecase.GetCurrentUserUseCase
import com.dannav.mibancamit.domain.usecase.LogOutUseCase
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val logOut: LogOutUseCase,
    private val currentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {


    private val _uiState = MutableStateFlow<Boolean?>(null)
    val uiState = _uiState.asStateFlow()

    fun logOut() {
        viewModelScope.launch {
            logOut.invoke()
        }
    }

    init {
        fun getCurrentUser() {
            viewModelScope.launch {
                _uiState.value = currentUserUseCase.invoke()
            }
        }

        getCurrentUser()
    }

}
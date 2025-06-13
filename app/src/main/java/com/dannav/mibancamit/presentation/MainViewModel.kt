package com.dannav.mibancamit.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dannav.mibancamit.domain.usecase.LogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val logOut: LogOutUseCase,
) : ViewModel() {

    fun logOut() {
        viewModelScope.launch {
            logOut.invoke()
        }
    }

}
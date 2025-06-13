package com.dannav.mibancamit.presentation.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dannav.mibancamit.data.Resource
import com.dannav.mibancamit.domain.usecase.PaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentUseCase: PaymentUseCase
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun onMakePayment(destinationCardNumber: String, fromCardId: String, amount: Double) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null, success = null) }
        when(val res = paymentUseCase(destinationCardNumber, fromCardId, amount)) {
            is Resource.Success -> _state.update { it.copy(isLoading = false, success = res.message) }
            is Resource.Failure -> _state.update { it.copy(isLoading = false, error = res.message) }
            is Resource.Loading -> _state.update { it.copy(isLoading = true) }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(error = null, success = null) }
    }
}

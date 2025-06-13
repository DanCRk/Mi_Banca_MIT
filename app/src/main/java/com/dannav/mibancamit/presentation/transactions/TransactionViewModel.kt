package com.dannav.mibancamit.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dannav.mibancamit.data.model.Transaction
import com.dannav.mibancamit.domain.UiState
import com.dannav.mibancamit.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
) : ViewModel() {


    private val _state = MutableStateFlow(UiState<Transaction>())
    val state: StateFlow<UiState<Transaction>> = _state

    init {
        getTransactionsUseCase()
            .onEach { list -> _state.update { it.copy(elements = list) } }
            .catch   {  _state.update { it.copy(error = "Error cargando tarjetas") } }
            .launchIn(viewModelScope)
    }

}

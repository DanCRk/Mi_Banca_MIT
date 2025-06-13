package com.dannav.mibancamit.presentation.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dannav.mibancamit.data.Resource
import com.dannav.mibancamit.data.model.Card
import com.dannav.mibancamit.domain.usecase.AddCardUseCase
import com.dannav.mibancamit.domain.usecase.ObserveCardsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCardsViewModel @Inject constructor(
    observeCards: ObserveCardsUseCase,
    private val addCard: AddCardUseCase
) : ViewModel() {

    data class UiState(
        val cards: List<Card> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init {
        observeCards()
            .onEach { list -> _state.update { it.copy(cards = list) } }
            .catch   {  _state.update { it.copy(error = "Error cargando tarjetas") } }
            .launchIn(viewModelScope)
    }

    fun onAddCard(holder: String, number: String, expiry: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null, success = null) }

        when (val res = addCard(holder, number, expiry)) {
            is Resource.Success -> _state.update {
                it.copy(isLoading = false, success = res.message)
            }
            is Resource.Failure -> _state.update {
                it.copy(isLoading = false, error = res.message)
            }
            else -> Unit
        }
    }

    fun clearMessages() =
        _state.update { it.copy(error = null, success = null) }
}
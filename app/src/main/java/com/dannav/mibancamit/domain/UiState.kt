package com.dannav.mibancamit.domain


data class UiState<T>(
    val elements: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null
)

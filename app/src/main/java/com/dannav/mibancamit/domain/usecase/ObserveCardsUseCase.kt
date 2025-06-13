package com.dannav.mibancamit.domain.usecase

import com.dannav.mibancamit.data.model.Card
import com.dannav.mibancamit.data.remote.DatabaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCardsUseCase @Inject constructor(
    private val repo: DatabaseRepository
) {
    operator fun invoke(): Flow<List<Card>> = repo.observeCards()
}
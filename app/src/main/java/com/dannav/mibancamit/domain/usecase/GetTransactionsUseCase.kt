package com.dannav.mibancamit.domain.usecase

import com.dannav.mibancamit.data.model.Transaction
import com.dannav.mibancamit.data.remote.DatabaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {
    operator fun invoke(): Flow<List<Transaction>> = databaseRepository.observeTransactions()
}

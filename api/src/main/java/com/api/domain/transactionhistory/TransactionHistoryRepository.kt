package com.api.domain.transactionhistory

import retrofit2.Response

interface TransactionHistoryRepository {
    suspend fun getTransactionHistoryAsync(): Response<ArrayList<Transaction>>
}

class TransactionHistoryRepositoryImpl(private val dataSource: TransactionHistoryDataSource) :
        TransactionHistoryRepository {
    override suspend fun getTransactionHistoryAsync() = dataSource.getTransactionHistoryAsync()
}
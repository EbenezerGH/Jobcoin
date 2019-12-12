package com.api.domain.sendtransaction

import retrofit2.Response

interface SendTransactionRepository {
    suspend fun sendTransactionAsync(
            fromAddress: String,
            toAddress: String,
            amount: String): Response<SendTransaction>
}

class SendTransactionRepositoryImpl(
        private val dataSourceSend: SendTransactionDataSource) : SendTransactionRepository {
    override suspend fun sendTransactionAsync(
            fromAddress: String,
            toAddress: String,
            amount: String) = dataSourceSend.sendTransaction(fromAddress, toAddress, amount)
}

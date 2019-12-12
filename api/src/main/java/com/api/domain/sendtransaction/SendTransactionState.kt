package com.api.domain.sendtransaction

sealed class SendTransactionState {
    object Loading : SendTransactionState()
    class Failure(val transaction: SendTransaction) : SendTransactionState()
    class Success(val transaction: SendTransaction) : SendTransactionState()
}
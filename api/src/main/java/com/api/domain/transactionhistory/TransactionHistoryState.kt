package com.api.domain.transactionhistory

sealed class TransactionHistoryState {
    object Loading : TransactionHistoryState()
    class Failure(val error: String) : TransactionHistoryState()
    class Success(val transaction: ArrayList<Transaction>) : TransactionHistoryState()
}
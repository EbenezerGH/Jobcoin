package com.api.domain.transactionhistory

data class Transaction(
        val timestamp: String,
        val fromAddress: String?,
        val toAddress: String,
        val amount: String
)
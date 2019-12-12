package com.api.domain.addressinfo

import com.api.domain.transactionhistory.Transaction

data class AddressInfo(
        var balance: String,
        var transactions: ArrayList<Transaction>
)
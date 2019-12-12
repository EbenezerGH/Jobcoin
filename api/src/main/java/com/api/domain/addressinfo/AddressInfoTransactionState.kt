package com.api.domain.addressinfo

sealed class AddressInfoTransactionState {
    object Loading : AddressInfoTransactionState()
    class Failure(val error: String) : AddressInfoTransactionState()
    class Success(val addressInfo: AddressInfo) : AddressInfoTransactionState()
}

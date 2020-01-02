package com.jobcoin.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.api.domain.addressinfo.AddressInfoRepository
import com.api.domain.addressinfo.AddressInfoTransactionState
import com.api.domain.sendtransaction.SendTransaction
import com.api.domain.sendtransaction.SendTransactionRepository
import com.api.domain.sendtransaction.SendTransactionState
import com.api.domain.sendtransaction.SendTransactionState.*
import com.api.domain.transactionhistory.TransactionHistoryRepository
import com.api.domain.transactionhistory.TransactionHistoryState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SendViewModel(
        private val repo: TransactionHistoryRepository,
        private val repoSend: SendTransactionRepository,
        private val repoAddressInfo: AddressInfoRepository) : ViewModel() {
    companion object {
        const val TAG = "SendViewModel"
    }

    private var mutableSendTransactionState = MutableLiveData<SendTransactionState>()
    val sendTransactionState = mutableSendTransactionState

    private var mutableFetchTransactionHistoryState = MutableLiveData<TransactionHistoryState>()
    val fetchTransactionHistoryState = mutableFetchTransactionHistoryState

    private val mutableFetchAddressInfoState = MutableLiveData<AddressInfoTransactionState>()
    val fetchAddressInfoState = mutableFetchAddressInfoState

    var address: String? = null
    var balance: String? = null
    var errorString: String = "Something Went Wrong"

    fun fetchTransactionHistory() {
        mutableFetchTransactionHistoryState.postValue(TransactionHistoryState.Loading)
        GlobalScope.launch {
            fetchTransactionHistoryResponse()
        }
    }

    private suspend fun fetchTransactionHistoryResponse() {
        val response = repo.getTransactionHistoryAsync()
        if (response.isSuccessful) {
            when (response.code()) {

                200 -> {
                    mutableFetchTransactionHistoryState.postValue(response.body()?.let { TransactionHistoryState.Success(it) })
                }
                else -> {
                    val error = "Unknown response code ${response.code()} :: ${response.message()}"
                    mutableFetchTransactionHistoryState.postValue(
                            TransactionHistoryState.Failure(error))
                    Log.e(TAG, error)
                }
            }
        } else {
            mutableFetchTransactionHistoryState.postValue(TransactionHistoryState.Failure(response.message()))
        }
    }

    fun sendTransaction(fromAddress: String, toAddress: String, amount: String) {
        mutableSendTransactionState.postValue(Loading)
        GlobalScope.launch {
            sendTransactionResponse(fromAddress, toAddress, amount)
        }
    }

    private suspend fun sendTransactionResponse(
            fromAddress: String,
            toAddress: String,
            amount: String
    ) {
        val response = repoSend.sendTransactionAsync(fromAddress, toAddress, amount)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    mutableSendTransactionState.postValue(
                            Success(
                                    SendTransaction("Transaction Sent ${response.body()}", "")
                            )
                    )
                }
                422 -> {
                    mutableSendTransactionState.postValue(
                            Failure(
                                    SendTransaction("", "Transaction Failed ${response.message()}")
                            )
                    )
                }
                else -> {
                    mutableSendTransactionState.postValue(
                            Failure(
                                    SendTransaction("", "Unknown Response Code ${response.code()}")
                            )
                    )
                }
            }
        } else {
            mutableSendTransactionState.postValue(
                    Failure(
                            SendTransaction("", "Transaction Unsuccessful ${response.message()}")
                    )
            )
        }
    }

    fun fetchAddressInfo() {
        mutableFetchAddressInfoState.postValue(AddressInfoTransactionState.Loading)
        GlobalScope.launch {
            fetchAddressInfoResponse(address!!)
        }
    }

    private suspend fun fetchAddressInfoResponse(address: String) {
        val response = repoAddressInfo.getAddressInfoAsync(address)
        when {
            response.code() == 200 -> {
                mutableFetchAddressInfoState.postValue(response.body()?.let {
                    AddressInfoTransactionState.Success(it)
                })
            }

            !response.isSuccessful -> {
                errorString = response.errorBody()?.string() ?: "Something Went Wrong"
                mutableFetchAddressInfoState.postValue(
                        AddressInfoTransactionState.Failure(errorString)
                )
            }
        }
    }

}
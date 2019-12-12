package com.jobcoin.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.api.domain.addressinfo.AddressInfo
import com.api.domain.addressinfo.AddressInfoRepository
import com.api.domain.addressinfo.AddressInfoTransactionState
import com.api.domain.addressinfo.AddressInfoTransactionState.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignInViewModel(private val repo: AddressInfoRepository) : ViewModel() {
    companion object {
        const val TAG = "SignInViewModel"
    }

    private val mutableSignInState = MutableLiveData<AddressInfoTransactionState>()
    val signInState = mutableSignInState

    lateinit var errorString: String

    fun signIn(address: String) {
        mutableSignInState.postValue(Loading)
        GlobalScope.launch {
            fetchSignInResponse(address)
        }
    }

    private suspend fun fetchSignInResponse(address: String) {
        val response = repo.getAddressInfoAsync(address)
        if (response.isSuccessful) {
            when {
                response.code() == 200 -> {
                    mutableSignInState.postValue(
                            Success(
                                    response.body() ?: AddressInfo("", ArrayList())
                            )
                    )
                }
                else -> {
                    errorString = "Unknown Response Code ${response.body()}"
                    Log.e(TAG, "Unknown Response Code ${response.code()} :: ${response.body()}")
                    mutableSignInState.postValue(Failure(errorString))
                }
            }
        } else {
            errorString = response.message() ?: "Something Went Wrong"
            mutableSignInState.postValue(Failure(errorString))
        }
    }

}
package com.api.domain.addressinfo

import retrofit2.Response

interface AddressInfoRepository {
    suspend fun getAddressInfoAsync(address: String): Response<AddressInfo>
}

class AddressInfoRepositoryImpl(private val dataSource: AddressInfoDataSource) :
        AddressInfoRepository {

    override suspend fun getAddressInfoAsync(address: String): Response<AddressInfo> =
            dataSource.getAddressInfoAsync(address)
}
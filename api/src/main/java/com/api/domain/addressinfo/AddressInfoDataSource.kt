package com.api.domain.addressinfo

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AddressInfoDataSource {
    @GET("/willpower-drank/api/addresses/{address}")
    suspend fun getAddressInfoAsync(@Path("address") address: String): Response<AddressInfo>
}
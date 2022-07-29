package com.certified.autopaper.data.network

import com.certified.autopaper.data.model.AccountResolveResponse
import com.certified.autopaper.data.model.Banks
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PaystackApiService {

    @GET("bank/resolve")
    suspend fun resolveAccount(
        @Header("Authorization") token: String,
        @Query("account_number") accountNumber: String,
        @Query("bank_code") bankCode: String
    ): Response<AccountResolveResponse>

    @GET("bank")
    suspend fun getBanks(
        @Header("Authorization") token: String
    ): Response<Banks>
}
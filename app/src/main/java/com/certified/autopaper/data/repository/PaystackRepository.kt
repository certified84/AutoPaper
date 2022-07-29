package com.certified.autopaper.data.repository

import com.certified.autopaper.data.network.PaystackApiService
import javax.inject.Inject

class PaystackRepository @Inject constructor(private val apiService: PaystackApiService) {

    suspend fun resolveAccount(token: String, accountNumber: String, bankCode: String) =
        apiService.resolveAccount(token, accountNumber, bankCode)

    suspend fun getBanks(token: String) = apiService.getBanks(token)
}
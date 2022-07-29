package com.certified.autopaper.data.model

data class AccountResolveResponse(
    val status: Boolean = false,
    val message: String = "",
    val data: Data = Data()
)

data class Data(
    val account_number: String = "",
    val account_name: String = "",
    val bank_id: Int = 0
)
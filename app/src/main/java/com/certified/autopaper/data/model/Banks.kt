package com.certified.autopaper.data.model

data class Banks(
    val status: Boolean = false,
    val message: String = "",
    val data: List<Bank> = listOf()
)

data class Bank(
    val name: String = "",
    val slug: String = "",
    val code: String = "",
    val longcode: String = "",
//    val gateway: null,
    val pay_with_bank: Boolean = false,
    val active: Boolean = false,
    val is_deleted: Boolean = false,
    val country: String = "",
    val currency: String = "",
    val type: String = "",
    val id: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)
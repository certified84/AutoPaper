package com.certified.autopaper.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Agent(
    val id: String = "",
    val name: String = "",
    val photoUrl: String? = null,
    val phoneNumber: String = "",
    val email: String = "",
    val state: String = "",
    val townResidence: String = "",
    val homeAddress: String = "",
    val bankName: String = "",
    val accountNumber: String = "",
    val accountType: String = "agent",
    val authType: String = "email",
    val commissionBalance: String = "",
    val complete: Boolean = false,
    val registeredVehicles: Int = 0,
    val ongoingRegistration: Int = 0,
) : Parcelable
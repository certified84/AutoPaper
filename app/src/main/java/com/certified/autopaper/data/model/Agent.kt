package com.certified.autopaper.data.model

data class Agent(
    val name: String = "",
    val accountType: String = "agent",
    val commissionBalance: String = "",
    val complete: Boolean = false,
    val registeredVehicles: Int = 0,
    val ongoingRegistration: Int = 0,
)
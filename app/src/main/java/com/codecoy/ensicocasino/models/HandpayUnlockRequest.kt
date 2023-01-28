package com.codecoy.ensicocasino.models

data class HandpayUnlockRequest(
    val date: String, val clientName: String, val type: String, val handpay: Handpays
)

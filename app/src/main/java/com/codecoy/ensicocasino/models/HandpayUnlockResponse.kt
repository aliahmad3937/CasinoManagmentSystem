package com.codecoy.ensicocasino.models

data class HandpayUnlockResponse(
    val date: String,
    val clientName: String,
    val type: String,
    val handpay: Handpays,
    val responseCode: Int,
    val reason: String
)

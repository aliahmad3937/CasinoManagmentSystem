package com.codecoy.ensicocasino.models

data class LoginResponse(
    val date: String,
    val clientName: String,
    val type: String,
    val username: String,
    val password: String,
    val reserved: String,
    val currency: String,
    val responseCode: Int,
    val reason: String
    )
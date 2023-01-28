package com.codecoy.ensicocasino.models

data class HandpaysRequest(
    val date: String, val clientName: String, val type: String, val filter: String
)

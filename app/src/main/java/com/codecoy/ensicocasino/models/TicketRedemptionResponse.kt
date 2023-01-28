package com.codecoy.ensicocasino.models

import java.io.Serializable

data class TicketRedemptionResponse(val date: String, val clientName: String, val type: String, val barcode: String, val responseCode: Int, val amount: Int, val reason: String)

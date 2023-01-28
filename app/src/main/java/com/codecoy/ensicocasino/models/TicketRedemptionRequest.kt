package com.codecoy.ensicocasino.models

import java.io.Serializable

data class TicketRedemptionRequest(val date: String, val clientName: String, val type: String, val barcode: String)

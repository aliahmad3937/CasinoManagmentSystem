package com.codecoy.ensicocasino.models

import com.google.gson.annotations.SerializedName

data class CashInRequest(
    var date         : String? = null,
    var clientName   : String? = null,
    var type         : String? = null,
    var machine      : String? = null,
    var amount       : Int?    = null,
    var responseCode : Int?    = null,
    var reason       : String? = null
) {

constructor(
    date: String,
    clientName: String,
    type: String,
    machine: String,
    amount: Int
):this(date = date, clientName = clientName, type = type, machine = machine, amount = amount, responseCode = null , reason=null)

    constructor(
        date: String,
        clientName: String,
        type: String,
        machine: String
    ):this(date = date, clientName = clientName, type = type, machine = machine, amount = null, responseCode = null , reason=null)
}

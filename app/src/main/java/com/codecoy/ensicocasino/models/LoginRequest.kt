package com.codecoy.ensicocasino.models

import java.io.Serializable

data class LoginRequest(val date: String, val clientName: String, val type: String, val username: String, val password: String, val reserved: String){

    constructor(date: String, clientName: String, type: String,username: String,):this(date,clientName,type,username,"","")
}




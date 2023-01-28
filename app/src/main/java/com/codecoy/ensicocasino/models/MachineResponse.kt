package com.codecoy.ensicocasino.models

import com.google.gson.annotations.SerializedName

data class MachineResponse (

    @SerializedName("date"         ) var date         : String?           = null,
    @SerializedName("clientName"   ) var clientName   : String?           = null,
    @SerializedName("type"         ) var type         : String?           = null,
    @SerializedName("reserved"     ) var reserved     : String?           = null,
    @SerializedName("machines"     ) var machines     : ArrayList<String> = arrayListOf(),
    @SerializedName("responseCode" ) var responseCode : Int?              = null

)

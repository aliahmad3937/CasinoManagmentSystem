package com.codecoy.ensicocasino.models

import com.google.gson.annotations.SerializedName

data class MachineRequest (

    @SerializedName("date"       ) var date       : String? = null,
    @SerializedName("clientName" ) var clientName : String? = null,
    @SerializedName("type"       ) var type       : String? = null,
    @SerializedName("reserved"   ) var reserved   : String? = null

)

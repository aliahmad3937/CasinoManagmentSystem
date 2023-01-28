package com.codecoy.ensicocasino.models

import com.google.gson.annotations.SerializedName

data class HandpaysResponse (
    @SerializedName("date"         ) var date         : String?             = null,
    @SerializedName("clientName"   ) var clientName   : String?             = null,
    @SerializedName("type"         ) var type         : String?             = null,
    @SerializedName("filter"       ) var filter       : String?             = null,
    @SerializedName("handpays"     ) var handpays     : ArrayList<Handpays> = arrayListOf(),
    @SerializedName("responseCode" ) var responseCode : Int?                = null

)


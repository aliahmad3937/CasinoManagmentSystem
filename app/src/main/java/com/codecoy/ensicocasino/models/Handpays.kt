package com.codecoy.ensicocasino.models

import com.google.gson.annotations.SerializedName

data class Handpays (

    @SerializedName("gm"     ) var gm     : String? = null,
    @SerializedName("type"   ) var type   : String? = null,
    @SerializedName("amount" ) var amount : Int?    = null

)

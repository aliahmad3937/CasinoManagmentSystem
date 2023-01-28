package com.codecoy.ensicocasino.callbacks

import com.codecoy.ensicocasino.models.Handpays

interface HandpayInterface {

    fun handpayClick(handpay: Handpays,amnt:String)
}
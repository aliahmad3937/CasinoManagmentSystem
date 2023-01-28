package com.codecoy.ensicocasino.repository

import android.util.Log
import com.codecoy.ensicocasino.models.*
import com.codecoy.ensicocasino.retrofit.RetrofitAPI
import retrofit2.Call

class MainRepository constructor(private val retrofitService: RetrofitAPI) {
    fun checkUser(body: LoginRequest) = retrofitService.checkUser(body)

    fun ticketRedemption(body: TicketRedemptionRequest) = retrofitService.ticketRedemption(body)


    fun handpayList(body: HandpaysRequest) = retrofitService.handpayList(body)


    fun machineList(body: MachineRequest) = retrofitService.machineList(body)


    fun cashIn(body: CashInRequest) = retrofitService.cashIn(body)

    fun handpayUnlock(body: HandpayUnlockRequest) = retrofitService.handpayUnlock(body)



}
package com.codecoy.ensicocasino.retrofit

import android.content.Context
import android.content.SharedPreferences
import com.codecoy.ensicocasino.models.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetrofitAPI {


    @Headers("Content-Type: application/json")
    @POST("mobile/")
    fun checkUser(@Body body:LoginRequest): Call<LoginResponse>


    @Headers("Content-Type: application/json")
    @POST("mobile/")
    fun ticketRedemption(@Body body:TicketRedemptionRequest): Call<TicketRedemptionResponse>


    @Headers("Content-Type: application/json")
    @POST("mobile/")
    fun handpayList(@Body body:HandpaysRequest): Call<HandpaysResponse>

    @Headers("Content-Type: application/json")
    @POST("mobile/")
    fun machineList(@Body body:MachineRequest): Call<MachineResponse>

    @Headers("Content-Type: application/json")
    @POST("mobile/")
    fun cashIn(@Body body:CashInRequest): Call<CashInRequest>

    @Headers("Content-Type: application/json")
    @POST("mobile/")
    fun handpayUnlock(@Body body:HandpayUnlockRequest): Call<HandpayUnlockResponse>

//    companion object {
//        var retrofitService: RetrofitAPI? = null
//        var context: Context? = null
//            set(value) {
//                field = value
//            }
//
//        val sharedPreferences: SharedPreferences =
//            Companion.context!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
//
//        fun getInstance(): RetrofitAPI {
//            if (retrofitService == null) {
//                val retrofit = Retrofit.Builder()
//                    .baseUrl(
//                        sharedPreferences.getString(
//                            "baseUrl",
//                            "https://93.103.81.142:51120/mobile/"
//                        )
//                    )
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build()
//                retrofitService = retrofit.create(RetrofitAPI::class.java)
//            }
//            return retrofitService!!
//        }
//    }
}
package com.codecoy.ensicocasino.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codecoy.ensicocasino.models.*
import com.codecoy.ensicocasino.repository.MainRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainViewModel constructor(private val repository: MainRepository)  : ViewModel() {

    val user = MutableLiveData<APIResponse>()
    val ticketRedemption = MutableLiveData<APIResponse>()
    val handpayList = MutableLiveData<APIResponse>()
    val machineList = MutableLiveData<APIResponse>()
    val handpayUnlock = MutableLiveData<APIResponse>()
    val cashIn = MutableLiveData<APIResponse>()



    fun logoutUser(body: LoginRequest) {
        user.postValue(APIResponse.Loading)
        val response:Call<LoginResponse> = repository.checkUser(body)

        response.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.code() == 200) {
                    user.postValue(APIResponse.Success(response.body()))
                }else{
                    user.postValue(APIResponse.Error("URL is not valid!"))
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                user.postValue(APIResponse.Error(t.localizedMessage))
            }
        })
    }


    fun ticketRedemption(body: TicketRedemptionRequest) {
        ticketRedemption.postValue(APIResponse.Loading)
        val response:Call<TicketRedemptionResponse> = repository.ticketRedemption(body)

        response.enqueue(object : Callback<TicketRedemptionResponse> {
            override fun onResponse(call: Call<TicketRedemptionResponse>, response: Response<TicketRedemptionResponse>) {
                ticketRedemption.postValue(APIResponse.Success (response.body()))
            }
            override fun onFailure(call: Call<TicketRedemptionResponse>, t: Throwable) {
                ticketRedemption.postValue(APIResponse.Error(t.localizedMessage))
            }
        })
    }


    fun handpayList(body: HandpaysRequest) {
        handpayList.postValue(APIResponse.Loading)
        val response:Call<HandpaysResponse> = repository.handpayList(body)

        response.enqueue(object : Callback<HandpaysResponse> {
            override fun onResponse(call: Call<HandpaysResponse>, response: Response<HandpaysResponse>) {
                handpayList.postValue(APIResponse.Success (response.body()))
            }
            override fun onFailure(call: Call<HandpaysResponse>, t: Throwable) {
                handpayList.postValue(APIResponse.Error(t.localizedMessage))
            }
        })
    }


    fun machineList(body: MachineRequest) {
        machineList.postValue(APIResponse.Loading)
        val response:Call<MachineResponse> = repository.machineList(body)

        response.enqueue(object : Callback<MachineResponse> {
            override fun onResponse(call: Call<MachineResponse>, response: Response<MachineResponse>) {
                machineList.postValue(APIResponse.Success (response.body()))
            }
            override fun onFailure(call: Call<MachineResponse>, t: Throwable) {
                machineList.postValue(APIResponse.Error(t.localizedMessage))
            }
        })
    }



    fun handpayUnlock(body: HandpayUnlockRequest) {
        handpayUnlock.postValue(APIResponse.Loading)
        val response:Call<HandpayUnlockResponse> = repository.handpayUnlock(body)

        response.enqueue(object : Callback<HandpayUnlockResponse> {
            override fun onResponse(call: Call<HandpayUnlockResponse>, response: Response<HandpayUnlockResponse>) {
                handpayUnlock.postValue(APIResponse.Success (response.body()))
            }
            override fun onFailure(call: Call<HandpayUnlockResponse>, t: Throwable) {
                handpayUnlock.postValue(APIResponse.Error(t.localizedMessage))
            }
        })
    }


    fun cashIn(body: CashInRequest) {
        cashIn.postValue(APIResponse.Loading)
        val response:Call<CashInRequest> = repository.cashIn(body)

        response.enqueue(object : Callback<CashInRequest> {
            override fun onResponse(call: Call<CashInRequest>, response: Response<CashInRequest>) {
                cashIn.postValue(APIResponse.Success (response.body()))
            }
            override fun onFailure(call: Call<CashInRequest>, t: Throwable) {
                cashIn.postValue(APIResponse.Error(t.localizedMessage))
            }
        })
    }


}

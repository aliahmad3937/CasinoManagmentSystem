package com.codecoy.ensicocasino.models

sealed class APIResponse{
    data class Success<T>(val data: T) : APIResponse()
    data class Error(val message: String) : APIResponse()
    object Loading : APIResponse()
}

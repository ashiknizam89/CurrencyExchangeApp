package com.example.currencyexchangeapp

import com.example.currencyexchangeapp.data.ExchangeResponse
import retrofit2.http.GET

interface ExchangeApi {

    @GET("v4/latest/EUR")
    suspend fun getRates(): ExchangeResponse
}

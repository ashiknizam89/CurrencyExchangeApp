package com.example.currencyexchangeapp.data

data class ExchangeResponse(
    val base: String,
    val rates: Map<String, Double>
)

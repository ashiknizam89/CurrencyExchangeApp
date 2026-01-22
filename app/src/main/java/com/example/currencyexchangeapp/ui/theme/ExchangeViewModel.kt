package com.example.currencyexchangeapp.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.currencyexchangeapp.ApiClient
import com.example.currencyexchangeapp.data.AppDatabase
import com.example.currencyexchangeapp.data.ExchangeRateEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExchangeViewModel(application: Application) : AndroidViewModel(application) {

    // ---------- DATABASE ----------
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "exchange-db"
    ).build()

    // ---------- RATES ----------
    private val _rates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val rates: StateFlow<Map<String, Double>> = _rates

    // ---------- CONVERTER STATE ----------
    private val _fromCurrency = MutableStateFlow("EUR")
    val fromCurrency: StateFlow<String> = _fromCurrency

    private val _toCurrency = MutableStateFlow("USD")
    val toCurrency: StateFlow<String> = _toCurrency

    private val _amount = MutableStateFlow("1")
    val amount: StateFlow<String> = _amount

    private val _convertedValue = MutableStateFlow(0.0)
    val convertedValue: StateFlow<Double> = _convertedValue

    init {
        loadFromDatabase()
        refreshFromApi()
    }

    // ---------- DB LOAD ----------
    private fun loadFromDatabase() {
        viewModelScope.launch {
            val cached = db.exchangeRateDao().getAllRates()
            _rates.value = cached.associate { it.currency to it.rate }
            calculateConversion()
        }
    }

    // ---------- API REFRESH ----------
    fun refreshFromApi() {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.getRates()
                val entities = response.rates.map {
                    ExchangeRateEntity(it.key, it.value)
                }

                db.exchangeRateDao().insertAll(entities)
                _rates.value = response.rates
                calculateConversion()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ---------- USER ACTIONS ----------
    fun setFromCurrency(currency: String) {
        _fromCurrency.value = currency
        calculateConversion()
    }

    fun setToCurrency(currency: String) {
        _toCurrency.value = currency
        calculateConversion()
    }

    fun setAmount(value: String) {
        _amount.value = value
        calculateConversion()
    }

    // ---------- CONVERSION LOGIC ----------
    private fun calculateConversion() {
        val amountValue = _amount.value.toDoubleOrNull() ?: return
        val fromRate = _rates.value[_fromCurrency.value] ?: return
        val toRate = _rates.value[_toCurrency.value] ?: return

        _convertedValue.value = (amountValue / fromRate) * toRate
    }

    fun swapCurrencies() {
        val temp = _fromCurrency.value
        _fromCurrency.value = _toCurrency.value
        _toCurrency.value = temp
        calculateConversion()
    }
}

package com.example.currencyexchangeapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ExchangeRateEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exchangeRateDao(): ExchangeRateDao
}

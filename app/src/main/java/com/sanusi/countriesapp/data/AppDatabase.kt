package com.sanusi.countriesapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sanusi.countriesapp.data.local.CountryDao
import com.sanusi.countriesapp.data.local.CountryEntity

@Database(entities = [CountryEntity ::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract val countryDao : CountryDao
}
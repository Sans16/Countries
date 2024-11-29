package com.sanusi.countriesapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.sanusi.countriesapp.utils.Constants.COUNTRY_ENTITY_NAME

@Dao
interface CountryDao {
    @Upsert
    suspend fun setAllCountries(availableCountries : List<CountryEntity>)

    @Query("DELETE FROM $COUNTRY_ENTITY_NAME")
    suspend fun clearCountries()

    @Query("SELECT * FROM $COUNTRY_ENTITY_NAME")
    suspend fun getAllCountries() : List<CountryEntity>

    @Query("SELECT * FROM $COUNTRY_ENTITY_NAME WHERE continent = :continent")
    suspend fun getCountriesByContinent(continent : String) : List<CountryEntity>

}
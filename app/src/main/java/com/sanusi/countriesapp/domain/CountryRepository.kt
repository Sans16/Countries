package com.sanusi.countriesapp.domain

import com.sanusi.countriesapp.utils.CountryResult
import kotlinx.coroutines.flow.Flow

interface CountryRepository {

    suspend fun getAllCountries(refreshData : Boolean) : Flow<CountryResult<List<CountryUI>>>

    suspend fun getCountriesByContinent(continent : String) : List<CountryUI>
}
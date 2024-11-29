package com.sanusi.countriesapp.data

import com.apollographql.apollo3.ApolloClient
import com.sanusi.CountriesQuery
import com.sanusi.countriesapp.data.local.CountryDao
import com.sanusi.countriesapp.data.mappers.toCountryEntity
import com.sanusi.countriesapp.data.mappers.toCountryUI
import com.sanusi.countriesapp.domain.CountryRepository
import com.sanusi.countriesapp.domain.CountryUI
import com.sanusi.countriesapp.utils.Constants.SIMULATION_DELAY
import com.sanusi.countriesapp.utils.CountryResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException

class CountryRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val countryDao : CountryDao
) : CountryRepository {

    override suspend fun getAllCountries(
        refreshData : Boolean
    ) : Flow<CountryResult<List<CountryUI>>> {
       return flow {
           emit(CountryResult.Loading(true))
           val savedData = countryDao.getAllCountries()
           val canLoadFromCache = savedData.isNotEmpty() && !refreshData

           if(canLoadFromCache){
               delay(SIMULATION_DELAY) // to simulate loading
               val uiEntities = savedData.map { it.toCountryUI() }
               emit(CountryResult.Loading(false))
               emit(CountryResult.Success(uiEntities))
               return@flow
           }

           val apiResult = try {
               apolloClient
                   .query(CountriesQuery())
                   .execute()
                   .data?.countries
           } catch (ex : Exception) {
               emit(CountryResult.Loading(false))
               emit(CountryResult.Error(message = ex.message))
               return@flow
           }
           catch (ex : IOException) {
               emit(CountryResult.Loading(false))
               emit(CountryResult.Error(message = ex.message))
               return@flow
           }

           apiResult?.let {
               countries ->
               val entities = countries
                   .mapIndexed { index, country ->
                       country.toCountryEntity(index)
                   }
               if(refreshData) {
                   countryDao.clearCountries()
               }
               countryDao.setAllCountries(entities)
               emit(CountryResult.Success(entities.map { it.toCountryUI()}))
               emit(CountryResult.Loading(false))
               return@flow
           }

           emit(CountryResult.Error(message = "Unknown Error!"))
           emit(CountryResult.Loading(false))
           return@flow
       }
    }

    override suspend fun getCountriesByContinent(continent: String): List<CountryUI> {
        return countryDao.getCountriesByContinent(continent).map { it.toCountryUI() }
    }
}
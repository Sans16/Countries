package com.sanusi.countriesapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanusi.countriesapp.domain.CountryRepository
import com.sanusi.countriesapp.utils.Constants.DEFAULT_CONTINENT
import com.sanusi.countriesapp.utils.Constants.SIMULATION_DELAY
import com.sanusi.countriesapp.utils.CountryResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryViewModel @Inject constructor(
    private val countryRepository : CountryRepository
) : ViewModel() {
    private val _countryState = MutableStateFlow(CountryState())
    val countryState = _countryState.asStateFlow()

    private val _uiEvent = Channel<CountryUIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        getAllCountries()
    }

    fun onEvent(event : CountryEvent) {
        when (event) {
            is CountryEvent.GetCountryByContinent -> getCountryByContinent(event.continent)
            CountryEvent.RefreshData -> getAllCountries(refreshData = true)
        }
    }

    private fun getAllCountries(
        refreshData : Boolean = false
    ) {
        viewModelScope.launch {
            countryRepository.getAllCountries(refreshData).collect{
                result ->
                when (result) {
                    is CountryResult.Loading -> {
                        _countryState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }

                    is CountryResult.Error -> {
                       _uiEvent.send(CountryUIEvent.ShowErrorMessage(result.message ?: "Unknown Error"))
                    }

                    is CountryResult.Success -> {
                        result.data?.let { countries ->
                            val newPageNumber = if (refreshData) 1 else countryState.value.pageNumber + 1
                            val newCountries = countries.shuffled()

                            _countryState.update {
                                it.copy(
                                    pageNumber = newPageNumber,
                                    countries = newCountries
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getCountryByContinent(continentIndex : String) {
        if(continentIndex == DEFAULT_CONTINENT) {
            getAllCountries()
            return
        }

        viewModelScope.launch {
            _countryState.update { it.copy(isLoading = true,
                countries = emptyList()) }
            delay(SIMULATION_DELAY)
            val countries = countryRepository.getCountriesByContinent(continentIndex).shuffled()
            _countryState.update {
                state ->
                state.copy(countries = countries, isLoading = false)
            }
        }
    }
}
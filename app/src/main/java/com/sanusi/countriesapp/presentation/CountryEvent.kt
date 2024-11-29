package com.sanusi.countriesapp.presentation

sealed class CountryEvent {
    data object RefreshData : CountryEvent()
    data class GetCountryByContinent(val continent : String) : CountryEvent()
}
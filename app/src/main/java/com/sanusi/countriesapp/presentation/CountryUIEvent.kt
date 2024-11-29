package com.sanusi.countriesapp.presentation

sealed class CountryUIEvent {
    data class ShowErrorMessage(val message : String) : CountryUIEvent()
}
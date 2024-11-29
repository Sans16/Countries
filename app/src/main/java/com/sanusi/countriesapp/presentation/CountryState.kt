package com.sanusi.countriesapp.presentation

import com.sanusi.countriesapp.domain.CountryUI

data class CountryState(
    val countries : List<CountryUI> = emptyList(),
    val pageNumber : Int = 1,
    val isLoading : Boolean = false
)

package com.sanusi.countriesapp.domain

data class CountryUI(
    val name : String,
    val capital : String,
    val code : String,
    val emoji : String,
    val continent : String,
    val backgroundResourceIndex : Int,
    val languages : List<String>
)

package com.sanusi.countriesapp.utils

import com.sanusi.graphqltestproject.R


object Constants {
    const val BASE_URL = "https://countries.trevorblades.com/graphql"
    const val COUNTRY_ENTITY_NAME = "country_entity"
    const val DATABASE_NAME = "country_database.db"
    const val SIMULATION_DELAY = 500L
    const val DEFAULT_CONTINENT = "All"

    val KNOWN_CONTINENTS = arrayOf(DEFAULT_CONTINENT,"Africa","Antarctica","Asia","Europe",
        "North America","Oceania","South America")

    fun getContinentImage(continentName : String) : Int{
        return when(continentName) {
            "Africa" -> R.drawable.ic_africa
            "Asia" -> R.drawable.ic_asia
            "Europe" -> R.drawable.ic_europe
            "North America" -> R.drawable.ic_north_america
            "Oceania" -> R.drawable.ic_oceania
            "South America" ->  R.drawable.ic_south_america
            "Antarctica" -> R.drawable.ic_antarctica
            else -> R.drawable.ic_world
        }
    }
}
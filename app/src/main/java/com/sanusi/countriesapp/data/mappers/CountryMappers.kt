package com.sanusi.countriesapp.data.mappers

import com.sanusi.CountriesQuery
import com.sanusi.countriesapp.data.local.CountryEntity
import com.sanusi.countriesapp.domain.CountryUI

fun CountriesQuery.Country.toCountryEntity(id : Int) : CountryEntity {
    return CountryEntity(
        id = id,
        name = name,
        code = code,
        emoji = emoji,
        capital = capital ?: "No capital found",
        continent = continent.name,
        backgroundResourceIndex = (0..10).random(),
        languages = try {
            languages.joinToString(",") { it.name}
        } catch (ex : Exception) {
            ""
        }
    )
}

fun CountryEntity.toCountryUI() : CountryUI {
    return CountryUI(
        name = name,
        code = code,
        emoji = emoji,
        capital = capital ,
        continent = continent,
        backgroundResourceIndex = backgroundResourceIndex,
        languages = try {
            languages.split(",")
        } catch (ex : Exception) {
            emptyList()
        }
    )
}
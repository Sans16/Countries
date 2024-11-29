package com.sanusi.countriesapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sanusi.countriesapp.utils.Constants.COUNTRY_ENTITY_NAME

@Entity(tableName = COUNTRY_ENTITY_NAME)
data class CountryEntity(
    @PrimaryKey
    val id : Int = 0,
    val name : String = "",
    val capital : String = "",
    val code : String = "",
    val emoji : String = "",
    val continent : String = "",
    val backgroundResourceIndex : Int,
    val languages : String = ""
)

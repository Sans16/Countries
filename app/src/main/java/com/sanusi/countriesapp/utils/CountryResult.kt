package com.sanusi.countriesapp.utils

sealed class CountryResult<T> (
    val data : T? = null,
    val message : String? = ""
) {
    class Loading<T>(val isLoading : Boolean = true) : CountryResult<T>()
    class Success<T>(data : T?) : CountryResult<T>(data)
    class Error<T>(data: T? = null, message : String?) : CountryResult<T>(data = data,message = message)
}
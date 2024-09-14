package com.rk.openweatherapp.data.remote.dto

data class TempDto(
    val temp: Double,       // Current temperature
    val feels_like: Double, // Feels like temperature
    val temp_min: Double,   // Minimum temperature
    val temp_max: Double,   // Maximum temperature
    val pressure: Int,      // Atmospheric pressure
    val humidity: Int       // Humidity percentage
)



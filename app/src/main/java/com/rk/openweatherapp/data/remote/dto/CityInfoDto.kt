package com.rk.openweatherapp.data.remote.dto

data class CityInfoDto(
    val city: CityDto, // Contains basic city info
    val cnt: Int, // Number of days in the forecast
    val cod: String, // Response code
    val list: List<WeatherResponseDto>, // List of weather forecasts for each day
    val message: Double // Additional message or data
)

package com.rk.openweatherapp.presentation.weather_detail

import com.rk.openweatherapp.domain.model.Weather

data class WeatherState(
    val isLoading: Boolean = false,
    val weather: Weather? = null,
    val error: String = ""
)

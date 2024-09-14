package com.rk.openweatherapp.presentation.weather_list

import com.rk.openweatherapp.domain.model.Weather

sealed class WeatherListState {
    data object Loading : WeatherListState()
    data class Success(val weatherList: List<Weather>) : WeatherListState()
    data class Error(val message: String) : WeatherListState()
}



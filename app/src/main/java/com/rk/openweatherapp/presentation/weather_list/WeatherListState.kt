package com.rk.openweatherapp.presentation.weather_list

import com.rk.openweatherapp.domain.model.Weather

sealed class WeatherListState {
    data object Idle : WeatherListState()  // No loading, idle state
    data object Loading : WeatherListState()
    data class Success(val weatherList: List<Weather>) : WeatherListState()
    data class Error(val message: String) : WeatherListState()
}

//sealed class WeatherListState {
//    object Loading : WeatherListState()
//    data class Success(val weatherList: List<Weather>) : WeatherListState()
//    data class Error(val message: String) : WeatherListState()
//}



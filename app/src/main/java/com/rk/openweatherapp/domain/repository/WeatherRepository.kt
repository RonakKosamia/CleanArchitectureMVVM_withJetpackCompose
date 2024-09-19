package com.rk.openweatherapp.domain.repository

import com.rk.openweatherapp.common.Resource
import com.rk.openweatherapp.data.remote.dto.ForecastWeatherResponseDto
import com.rk.openweatherapp.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeatherByCity(city: String): Flow<Resource<List<Weather>>>
    suspend fun fetchCityWeather(city: String): ForecastWeatherResponseDto
    //suspend fun getWeatherList(lat: Double, lon: Double): List<Weather>
    suspend fun getWeatherList(lat: Double, lon: Double): Flow<Resource<List<Weather>>>
}


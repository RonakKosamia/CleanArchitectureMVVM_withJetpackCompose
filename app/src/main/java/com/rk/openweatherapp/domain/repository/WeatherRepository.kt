package com.rk.openweatherapp.domain.repository

import com.rk.openweatherapp.data.remote.dto.CityInfoDto
import com.rk.openweatherapp.domain.model.Weather

interface WeatherRepository {
    suspend fun fetchCityWeather(city: String): CityInfoDto
    suspend fun getWeatherList(lat: Double, lon: Double): List<Weather>
}

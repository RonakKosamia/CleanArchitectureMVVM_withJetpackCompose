package com.rk.openweatherapp.data.repository

import com.rk.openweatherapp.data.remote.OpenWeatherApi
import com.rk.openweatherapp.data.remote.dto.CityInfoDto
import com.rk.openweatherapp.data.remote.dto.toWeather
import com.rk.openweatherapp.domain.model.Weather
import com.rk.openweatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: OpenWeatherApi
) : WeatherRepository {

    // Fetch city weather by city name
    override suspend fun fetchCityWeather(city: String): CityInfoDto {
        return api.getCityInfo(city = city)
    }

    override suspend fun getWeatherList(lat: Double, lon: Double): List<Weather> {
        // Fetch the weather data from the API
        val weatherResponseDto = api.getWeatherList(lat, lon)
        // Directly map WeatherResponseDto to Weather using the toWeather() function
        return listOf(weatherResponseDto.toWeather()) // Wrap the result in a list
    }
}

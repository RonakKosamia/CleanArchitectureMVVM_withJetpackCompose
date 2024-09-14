package com.rk.openweatherapp.data.repository

import com.rk.openweatherapp.data.remote.dto.CityInfoDto
import com.rk.openweatherapp.data.remote.dto.WeatherResponseDto
import com.rk.openweatherapp.data.remote.dto.toWeather
import com.rk.openweatherapp.domain.model.Weather
import com.rk.openweatherapp.domain.repository.WeatherRepository
import java.io.IOException


class FakeWeatherRepository : WeatherRepository {

    private var weatherResponseList = listOf<WeatherResponseDto>()
    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    fun initList(weatherResponseList: List<WeatherResponseDto>) {
        this.weatherResponseList = weatherResponseList
    }

    override suspend fun fetchCityWeather(city: String): CityInfoDto {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherList(lat: Double, lon: Double): List<Weather> {
        if (shouldReturnNetworkError) {
            throw IOException() // Simulate network error
        } else {
            // Map WeatherResponseDto to Weather using toWeather()
            return weatherResponseList.map { weatherResponseDto ->
                weatherResponseDto.toWeather() // Ensure toWeather() returns Weather
            }
        }
    }
}

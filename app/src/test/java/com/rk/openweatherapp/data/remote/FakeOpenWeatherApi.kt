package com.rk.openweatherapp.data.remote

import com.rk.openweatherapp.data.remote.dto.CityInfoDto
import com.rk.openweatherapp.data.remote.dto.WeatherResponseDto

/*
*
*     val fakeApi = FakeOpenWeatherApi()
*     // Initialize with test data
*     fakeApi.initCityInfoDto(testCityInfoDto)
*     fakeApi.initWeatherResponseDto(testWeatherResponseDto)
*     // Use fakeApi in tests as a dependency for your repository or use cases
*
*  */

class FakeOpenWeatherApi : OpenWeatherApi {

    // Fake data to simulate city info and weather responses
    private var cityInfoDto: CityInfoDto? = null
    private var weatherResponseDto: WeatherResponseDto? = null

    // Initialize fake city info data
    fun initCityInfoDto(cityInfoDto: CityInfoDto) {
        this.cityInfoDto = cityInfoDto
    }

    // Initialize fake weather response data
    fun initWeatherResponseDto(weatherResponseDto: WeatherResponseDto) {
        this.weatherResponseDto = weatherResponseDto
    }

    // Simulated API call for fetching city info based on query parameters
    override suspend fun getCityInfo(
        city: String,
        mode: String,
        units: String,
        count: Int,
        apiKey: String
    ): CityInfoDto {
        // Returning a predefined cityInfoDto for testing
        return cityInfoDto ?: throw Exception("CityInfoDto not initialized")
    }

    // Simulated API call for fetching weather data based on lat/lon
    override suspend fun getWeatherList(
        lat: Double,
        lon: Double,
        apiKey: String
    ): WeatherResponseDto {
        // Returning a predefined weatherResponseDto for testing
        return weatherResponseDto ?: throw Exception("WeatherResponseDto not initialized")
    }
}

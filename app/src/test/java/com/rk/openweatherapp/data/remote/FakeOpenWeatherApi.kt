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

    // Error flags to simulate failure cases
    private var throwCityInfoError: Boolean = false
    private var throwWeatherError: Boolean = false



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
        if (throwCityInfoError) throw FakeApiException("Simulated CityInfo API Error")
        return cityInfoDto ?: throw FakeApiException("CityInfoDto not initialized")
    }

    // Simulated API call for fetching weather data based on lat/lon
    override suspend fun getWeatherList(
        lat: Double,
        lon: Double,
        apiKey: String
    ): WeatherResponseDto {
        if (throwWeatherError) throw FakeApiException("Simulated Weather API Error")
        return weatherResponseDto ?: throw FakeApiException("WeatherResponseDto not initialized")
    }

    // Simulated API call for fetching weather by city name
    override suspend fun getWeatherByCity(city: String, apiKey: String): WeatherResponseDto {
        if (throwWeatherError) throw FakeApiException("Simulated WeatherByCity API Error")
        return weatherResponseDto ?: throw FakeApiException("WeatherResponseDto not initialized")
    }

    // Methods to simulate error states in tests
    fun simulateCityInfoError() {
        throwCityInfoError = true
    }

    fun simulateWeatherError() {
        throwWeatherError = true
    }

    // Reset error states for testing different scenarios
    fun resetErrors() {
        throwCityInfoError = false
        throwWeatherError = false
    }

    // Custom exception for fake API errors
    class FakeApiException(message: String) : Exception(message)
}


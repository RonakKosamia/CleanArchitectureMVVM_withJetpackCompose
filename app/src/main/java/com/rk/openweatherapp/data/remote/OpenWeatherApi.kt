package com.rk.openweatherapp.data.remote

import com.rk.openweatherapp.BuildConfig
import com.rk.openweatherapp.data.remote.dto.ForecastWeatherResponseDto
import com.rk.openweatherapp.data.remote.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    // Get city weather by city name, dynamically passing the city and API key
    @GET("data/2.5/forecast/daily")
    suspend fun getCityInfo(
        @Query("q") city: String,
        @Query("mode") mode: String = "json",
        @Query("units") units: String = "metric",
        @Query("cnt") count: Int = 16,
        @Query("appid") apiKey: String = BuildConfig.CITY_DATA_OPEN_WEATHER_API_KEY // Default API key
    ): ForecastWeatherResponseDto

    @GET("data/2.5/weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY2
    ): WeatherResponseDto

    // Get weather list by latitude and longitude, dynamically passing the lat/lon and API key
    @GET("data/2.5/weather")
    suspend fun getWeatherList(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY // Default API key
    ): WeatherResponseDto


}

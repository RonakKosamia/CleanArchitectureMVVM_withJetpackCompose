package com.rk.openweatherapp.data.repository

import android.util.Log
import com.rk.openweatherapp.common.Resource
import com.rk.openweatherapp.data.remote.OpenWeatherApi
import com.rk.openweatherapp.data.remote.dto.CityInfoDto
import com.rk.openweatherapp.data.remote.dto.toWeather
import com.rk.openweatherapp.domain.model.Weather
import com.rk.openweatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: OpenWeatherApi
) : WeatherRepository {

    // Fetch city weather by city name
    override suspend fun fetchCityWeather(city: String): CityInfoDto {
        return api.getCityInfo(city = city)
    }

    override suspend fun getWeatherByCity(city: String): Flow<Resource<List<Weather>>>{
        return flow {
            try {
                emit(Resource.Loading())  // Emit loading state
                val response = api.getWeatherByCity(city)
                Log.d("WeatherApp", "City: ${response.name}")
                Log.d("WeatherApp", "Weather API response: $response")

                val weatherList = response.toWeather()   // Convert each WeatherResponseDto to Weather
                emit(Resource.Success(listOf(weatherList)))  // Emit success state with weather data
            } catch (e: Exception) {
                emit(Resource.Error("Failed to fetch weather: ${e.message}"))  // Emit error state
            }
        }
    }

    override suspend fun getWeatherList(lat: Double, lon: Double): Flow<Resource<List<Weather>>> {
        return flow {
            try {
                emit(Resource.Loading())  // Emit loading state
                val response = api.getWeatherList(lat, lon)
                val weatherList = listOf(response.toWeather())  // Convert the WeatherResponseDto to Weather
                emit(Resource.Success(weatherList))  // Emit success state with weather data
            } catch (e: Exception) {
                emit(Resource.Error("Failed to fetch weather: ${e.message}"))  // Emit error state
            }
        }
    }


}

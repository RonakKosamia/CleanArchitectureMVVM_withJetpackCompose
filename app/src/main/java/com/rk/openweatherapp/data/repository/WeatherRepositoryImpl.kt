package com.rk.openweatherapp.data.repository

import android.util.Log
import com.rk.openweatherapp.common.Resource
import com.rk.openweatherapp.data.remote.OpenWeatherApi
import com.rk.openweatherapp.data.remote.dto.ForecastWeatherResponseDto
import com.rk.openweatherapp.data.remote.dto.toWeather
import com.rk.openweatherapp.domain.model.Weather
import com.rk.openweatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: OpenWeatherApi
) : WeatherRepository {

    // A counter to keep track of how many API calls have been made
    private var apiCallCount = 0

    // Fetch city weather by city name
    override suspend fun fetchCityWeather(city: String): ForecastWeatherResponseDto {
        incrementApiCallCount() // Increment API call count for each request
        return api.getCityInfo(city = city)
    }

    override suspend fun getWeatherByCity(city: String): Flow<Resource<List<Weather>>> {
        return flow {
            try {
//                val responseOfForecastCall = fetchCityWeather(city)
//                Log.d("WeatherApp", "City: ${responseOfForecastCall.city.name}")
//                Log.d("WeatherApp", "Weather API response: $responseOfForecastCall")
                emit(Resource.Loading())  // Emit loading state
                incrementApiCallCount()   // Increment API call count for each request
                val response = api.getWeatherByCity(city)
                Log.d("WeatherApp", "City: ${response.name}")
                Log.d("WeatherApp", "Weather API response: $response")

                val weatherList = response.toWeather()   // Convert WeatherResponseDto to Weather
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
                incrementApiCallCount()   // Increment API call count for each request
                val response = api.getWeatherList(lat, lon)
                val weatherList = listOf(response.toWeather())  // Convert the WeatherResponseDto to Weather
                emit(Resource.Success(weatherList))  // Emit success state with weather data
            } catch (e: Exception) {
                emit(Resource.Error("Failed to fetch weather: ${e.message}"))  // Emit error state
            }
        }
    }

    // Helper method to increment the API call count and log the count
    private fun incrementApiCallCount() {
        apiCallCount++
        Log.d("WeatherApp", "API Call Count: $apiCallCount")
    }

    // Optional: A method to get the current count of API calls
    fun getApiCallCount(): Int {
        return apiCallCount
    }
}

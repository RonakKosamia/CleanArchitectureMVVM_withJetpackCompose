package com.rk.openweatherapp.data.repository

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

    override suspend fun getWeatherList(lat: Double, lon: Double): List<Weather> {
        // Fetch the weather data from the API
        val weatherResponseDto = api.getWeatherList(lat, lon)
        // Directly map WeatherResponseDto to Weather using the toWeather() function
        return listOf(weatherResponseDto.toWeather()) // Wrap the result in a list
    }

    override suspend fun getWeatherByCity(city: String): Flow<Resource<List<Weather>>> {
        return flow {
            try {
                emit(Resource.Loading())  // Emit loading state
                val response = api.getCityInfo(city)  // API call to get city weather
                val weatherList = response.list.map { it.toWeather() }  // Convert each WeatherResponseDto to Weather
                emit(Resource.Success(weatherList))  // Emit success state with weather data
            } catch (e: Exception) {
                emit(Resource.Error("Failed to fetch weather: ${e.message}"))  // Emit error state
            }
        }
    }
    suspend fun getCityForecast(city: String): Resource<List<Weather>> {
        return try {
            val response = api.getCityInfo(city)
            Resource.Success(response.list.map { it.toWeather() }) // Map the list of `WeatherResponseDto` to `Weather`
        } catch (e: Exception) {
            Resource.Error("Failed to fetch city forecast")
        }
    }

    suspend fun getCurrentWeatherByCity(city: String): Resource<Weather> {
        return try {
            val response = api.getWeatherByCity(city)
            Resource.Success(response.toWeather())
        } catch (e: Exception) {
            Resource.Error("Failed to fetch current weather")
        }
    }


}

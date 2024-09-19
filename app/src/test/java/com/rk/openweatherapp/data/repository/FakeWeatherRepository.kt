package com.rk.openweatherapp.data.repository

import com.rk.openweatherapp.common.Resource
import com.rk.openweatherapp.data.remote.dto.ForecastWeatherResponseDto
import com.rk.openweatherapp.data.remote.dto.WeatherResponseDto
import com.rk.openweatherapp.data.remote.dto.toWeather
import com.rk.openweatherapp.domain.model.Weather
import com.rk.openweatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeWeatherRepository : WeatherRepository {

    private var weatherResponseList = listOf<WeatherResponseDto>()
    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    fun initList(weatherResponseList: List<WeatherResponseDto>) {
        this.weatherResponseList = weatherResponseList
    }

    override suspend fun getWeatherByCity(city: String): Flow<Resource<List<Weather>>> {
        return flow {
            if (shouldReturnNetworkError) {
                emit(Resource.Error<List<Weather>>("Network Error"))
            } else {
                val weatherList = weatherResponseList.map { it.toWeather() }
                emit(Resource.Loading())
                emit(Resource.Success(weatherList))
            }
        }
    }

    override suspend fun fetchCityWeather(city: String): ForecastWeatherResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherList(lat: Double, lon: Double): Flow<Resource<List<Weather>>> {
        return flow {
            if (shouldReturnNetworkError) {
                emit(Resource.Error<List<Weather>>("Network Error"))
            } else {
                val weatherList = weatherResponseList.map { it.toWeather() }
                emit(Resource.Loading())
                emit(Resource.Success(weatherList))
            }
        }
    }
}

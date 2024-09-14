package com.rk.openweatherapp.domain.use_case.get_weather_detail

import com.rk.openweatherapp.common.Resource
import com.rk.openweatherapp.domain.model.Weather
import com.rk.openweatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetWeatherDetailUseCase @Inject constructor(
    private val repository: WeatherRepository
) {

    operator fun invoke(lat: Double, lon: Double, date: Int): Flow<Resource<Weather>> = flow {
        try {
            emit(Resource.Loading<Weather>())

            // Collect the flow emitted by the repository
            repository.getWeatherList(lat, lon).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Extract the weather list and apply find
                        val weatherList = resource.data
                        val weather = weatherList?.find { it.dt == date }

                        // Emit success if found, else emit error
                        if (weather != null) {
                            emit(Resource.Success(weather))
                        } else {
                            emit(Resource.Error<Weather>("Weather data for the given date not found"))
                        }
                    }
                    is Resource.Error -> {
                        emit(Resource.Error<Weather>(resource.message ?: "An unexpected error occurred"))
                    }
                    is Resource.Loading -> {
                        emit(Resource.Loading<Weather>())
                    }
                }
            }
        } catch (e: HttpException) {
            emit(Resource.Error<Weather>(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error<Weather>("Couldn't reach server. Check your internet connection"))
        }
    }
}


package com.rk.openweatherapp.domain.use_case.get_weather_list

import com.rk.openweatherapp.common.Resource
import com.rk.openweatherapp.domain.model.Weather
import com.rk.openweatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class GetWeatherListUseCase @Inject constructor(
    private val repository: WeatherRepository
) {

    operator fun invoke(lat: Double, lon: Double): Flow<Resource<List<Weather>>> = flow {
        try {
            emit(Resource.Loading<List<Weather>>())
            // Fetch the list of Weather directly from the repository
            val weatherList = repository.getWeatherList(lat, lon)
            emit(Resource.Success(weatherList))
        } catch (e: HttpException) {
            emit(
                Resource.Error<List<Weather>>(
                    e.localizedMessage ?: "An unexpected error occurred"
                )
            )
        } catch (e: IOException) {
            emit(Resource.Error<List<Weather>>("Couldn't reach server. Check your internet connection"))
        }
    }
}



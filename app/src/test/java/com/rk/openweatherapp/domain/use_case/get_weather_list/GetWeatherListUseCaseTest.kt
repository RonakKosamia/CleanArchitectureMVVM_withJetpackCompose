package com.rk.openweatherapp.domain.use_case.get_weather_list

import com.rk.openweatherapp.common.Resource
import com.rk.openweatherapp.data.remote.dto.toWeather
import com.rk.openweatherapp.data.repository.FakeWeatherRepository
import com.rk.openweatherapp.domain.model.Weather
import com.rk.openweatherapp.generateWeatherDtoList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test

class GetWeatherListUseCaseTest {

    private lateinit var getWeatherListUseCase: GetWeatherListUseCase
    private lateinit var fakeWeatherRepository: FakeWeatherRepository

    @Before
    fun setUp() {
        fakeWeatherRepository = FakeWeatherRepository()
        getWeatherListUseCase = GetWeatherListUseCase(fakeWeatherRepository)
    }

    @Test
    fun `Should return weatherDtoList successfully by city`() = runBlockingTest {
        val weatherDtoList = generateWeatherDtoList()
        val weatherList = weatherDtoList.map { it.toWeather() }
        val expectedResult = flow<Resource<List<Weather>>> {
            emit(Resource.Loading<List<Weather>>())
            emit(Resource.Success<List<Weather>>(weatherList))
        }

        fakeWeatherRepository.initList(weatherDtoList)

        // Update to fetch weather by city
        val result = getWeatherListUseCase.getWeatherByCity("London")

        result.first().data.`should be equal to`(expectedResult.first().data)
        result.last().data.`should be equal to`(expectedResult.last().data)
    }

    @Test
    fun `Should return weatherDtoList successfully by lat and lon`() = runBlockingTest {
        val weatherDtoList = generateWeatherDtoList()
        val weatherList = weatherDtoList.map { it.toWeather() }
        val expectedResult = flow<Resource<List<Weather>>> {
            emit(Resource.Loading<List<Weather>>())
            emit(Resource.Success<List<Weather>>(weatherList))
        }

        fakeWeatherRepository.initList(weatherDtoList)

        // Update to fetch weather by lat/lon
        val result = getWeatherListUseCase.getWeatherByLatLon(10.0, -15.0)

        result.first().data.`should be equal to`(expectedResult.first().data)
        result.last().data.`should be equal to`(expectedResult.last().data)
    }

    @Test
    fun `Should return exception when getting weatherList is unsuccessful`() = runBlockingTest {
        val expectedResult = flow<Resource<List<Weather>>> {
            emit(Resource.Loading<List<Weather>>())
            emit(Resource.Error<List<Weather>>("Couldn't reach server. Check your internet connection"))
        }

        fakeWeatherRepository.setShouldReturnNetworkError(true)

        try {
            // Update to fetch weather by lat/lon
            val result = getWeatherListUseCase.getWeatherByLatLon(10.00, -10.00)
            result.first().data.`should be equal to`(expectedResult.first().data)
            result.last().data.`should be equal to`(expectedResult.last().data)
            result.first().message.`should be equal to`(expectedResult.first().message)
            result.last().message.`should be equal to`(expectedResult.last().message)
        } catch (_: Exception) {
        }
    }
}

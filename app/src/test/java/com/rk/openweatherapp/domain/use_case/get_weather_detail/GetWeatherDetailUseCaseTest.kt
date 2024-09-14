package com.rk.openweatherapp.domain.use_case.get_weather_detail

import com.rk.openweatherapp.common.Resource
import com.rk.openweatherapp.data.remote.dto.toWeather
import com.rk.openweatherapp.data.repository.FakeWeatherRepository
import com.rk.openweatherapp.domain.model.Weather
import com.rk.openweatherapp.generateWeatherDtoList
import com.rk.openweatherapp.randomInt
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test

class GetWeatherDetailUseCaseTest {

    private lateinit var getWeatherDetailUseCase: GetWeatherDetailUseCase
    private lateinit var fakeWeatherRepository: FakeWeatherRepository

    @Before
    fun setUp() {
        fakeWeatherRepository = FakeWeatherRepository()
        getWeatherDetailUseCase = GetWeatherDetailUseCase(fakeWeatherRepository)
    }

    @Test
    fun `Should return Weather successfully`() = runBlockingTest {
        val weatherDtoList = generateWeatherDtoList()
        val weatherList = weatherDtoList.map { it.toWeather() }
        val weather = weatherList.random()
        val dateInput = weather.dt
        val expectedResult = flow {
            emit(Resource.Loading<Weather>())
            emit(Resource.Success<Weather>(weather))
        }

        fakeWeatherRepository.initList(weatherDtoList)

        val result = getWeatherDetailUseCase.invoke(10.0, 10.0, dateInput)

        result.first().data.`should be equal to`(expectedResult.first().data)
        result.last().data.`should be equal to`(expectedResult.last().data)
    }

    @Test
    fun `Should return exception when getting Weather is unsuccessful`() = runBlockingTest {
        val expectedResult = flow {
            emit(Resource.Loading<Weather>())
            emit(Resource.Error<Weather>("Couldn't reach server. Check your internet connection"))
        }

        fakeWeatherRepository.setShouldReturnNetworkError(true)

        try {
            val result = getWeatherDetailUseCase.invoke(10.0, 10.0, randomInt())
            result.first().data.`should be equal to`(expectedResult.first().data)
            result.last().data.`should be equal to`(expectedResult.last().data)
            result.first().message.`should be equal to`(expectedResult.first().message)
            result.last().message.`should be equal to`(expectedResult.last().message)
        } catch (_: Exception) {
        }
    }
}
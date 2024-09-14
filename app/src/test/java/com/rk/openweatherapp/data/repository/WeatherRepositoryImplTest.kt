package com.rk.openweatherapp.data.repository

import com.rk.openweatherapp.data.remote.FakeOpenWeatherApi
import com.rk.openweatherapp.generateCityInfoDto
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test

class WeatherRepositoryImplTest {

    private lateinit var weatherRepositoryImpl: WeatherRepositoryImpl
    private lateinit var fakeOpenWeatherApi: FakeOpenWeatherApi

    @Before
    fun setUp() {
        fakeOpenWeatherApi = FakeOpenWeatherApi()
        weatherRepositoryImpl = WeatherRepositoryImpl(fakeOpenWeatherApi)
    }

    @Test
    fun `Should return CityInfoDto successfully`() = runBlockingTest {
        val cityInfoDto = generateCityInfoDto()
        val expectedResult = cityInfoDto.list

        fakeOpenWeatherApi.initCityInfoDto(cityInfoDto)

        val result = weatherRepositoryImpl.getWeatherList(10.0, 10.0)

        result.`should be equal to`(expectedResult)
    }
}
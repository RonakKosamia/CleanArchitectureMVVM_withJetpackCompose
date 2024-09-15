package com.rk.openweatherapp.data.repository

import com.rk.openweatherapp.data.remote.FakeOpenWeatherApi
import com.rk.openweatherapp.data.remote.dto.CloudsDto
import com.rk.openweatherapp.data.remote.dto.CoordDto
import com.rk.openweatherapp.data.remote.dto.SysDto
import com.rk.openweatherapp.data.remote.dto.TempDto
import com.rk.openweatherapp.data.remote.dto.WeatherInfoDto
import com.rk.openweatherapp.data.remote.dto.WeatherResponseDto
import com.rk.openweatherapp.data.remote.dto.WindDto
import com.rk.openweatherapp.generateCityInfoDto
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test

class WeatherRepositoryImplTest {

    private lateinit var weatherRepositoryImpl: WeatherRepositoryImpl
    private lateinit var fakeOpenWeatherApi: FakeOpenWeatherApi

    val testWeatherResponseDto = WeatherResponseDto(
        coord = CoordDto(lon = -0.1257, lat = 51.5085),
        weather = listOf(WeatherInfoDto(main = "Clouds", description = "overcast clouds", icon = "04n", id = 0)),
        main = TempDto(
            temp = 288.46,
            feels_like = 287.85,
            temp_min = 286.4,
            temp_max = 289.31,
            pressure = 1029,
            humidity = 69
        ),
        wind = WindDto(speed = 5.0, 142, 1.0),
        sys = SysDto(country = "US", sunrise = 1625644800, sunset = 1625691600),
        name = "New York",
        visibility = 10000,
        clouds = CloudsDto(all = 90),
        dt = 1625670000
    )

    @Before
    fun setUp() {
        fakeOpenWeatherApi = FakeOpenWeatherApi()
        fakeOpenWeatherApi.initWeatherResponseDto(testWeatherResponseDto)
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
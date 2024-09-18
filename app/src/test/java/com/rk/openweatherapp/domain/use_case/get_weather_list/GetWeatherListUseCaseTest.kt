package com.rk.openweatherapp.domain.use_case.get_weather_list

import com.rk.openweatherapp.common.Resource
import com.rk.openweatherapp.data.remote.dto.toWeather
import com.rk.openweatherapp.data.repository.FakeWeatherRepository
import com.rk.openweatherapp.domain.model.Weather
import com.rk.openweatherapp.generateWeatherDtoList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
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
    fun `Should return weatherDtoList successfully by city`() = runTest {
        val weatherDtoList = generateWeatherDtoList()
        val weatherList = weatherDtoList.map { it.toWeather() }

        // Expected result doesn't need instance comparison
        val expectedResult = listOf(
            Resource.Loading<List<Weather>>(),
            Resource.Success<List<Weather>>(weatherList)
        )

        // Initialize the repository with the fake weather data
        fakeWeatherRepository.initList(weatherDtoList)

        // Fetch weather by city
        val result = mutableListOf<Resource<List<Weather>>>()
        getWeatherListUseCase.getWeatherByCity("London").collect { result.add(it) }

        // Compare the emitted results by checking the type instead of exact instance
        result.first()::class `should be equal to` Resource.Loading::class
        result.last().data `should be equal to` expectedResult.last().data
    }


    @Test
    fun `Should return weatherDtoList successfully by lat and lon`() = runTest {
        val weatherDtoList = generateWeatherDtoList()
        val weatherList = weatherDtoList.map { it.toWeather() }

        // Expected result for testing purposes
        val expectedResult = listOf(
            Resource.Loading<List<Weather>>(), // Loading state
            Resource.Success<List<Weather>>(weatherList) // Success state
        )

        // Initialize the fake repository with the test weather data
        fakeWeatherRepository.initList(weatherDtoList)

        // Collect the emitted results from the use case
        val result = mutableListOf<Resource<List<Weather>>>()
        getWeatherListUseCase.getWeatherByLatLon(10.0, -15.0).collect { result.add(it) }

        // Compare the emitted results by type for the loading state
        result.first()::class `should be equal to` Resource.Loading::class
        // Compare the actual data in the success state
        result.last().data `should be equal to` expectedResult.last().data
    }


    @Test
    fun `Should return exception when getting weatherList is unsuccessful`() = runTest {
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

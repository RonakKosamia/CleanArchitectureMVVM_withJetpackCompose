package com.rk.openweatherapp

import com.rk.openweatherapp.data.remote.dto.CityDto
import com.rk.openweatherapp.data.remote.dto.CityInfoDto
import com.rk.openweatherapp.data.remote.dto.CloudsDto
import com.rk.openweatherapp.data.remote.dto.CoordDto
import com.rk.openweatherapp.data.remote.dto.SysDto
import com.rk.openweatherapp.data.remote.dto.TempDto
import com.rk.openweatherapp.data.remote.dto.WeatherInfoDto
import com.rk.openweatherapp.data.remote.dto.WeatherResponseDto
import com.rk.openweatherapp.data.remote.dto.WindDto
import java.util.concurrent.ThreadLocalRandom

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
val random: ThreadLocalRandom
    get() = ThreadLocalRandom.current()

fun generateWeatherDtoList(
    size: Int = randomPositiveInt(10),
    creationFunction: (Int) -> WeatherResponseDto = { generateWeatherResponseDto() }
): List<WeatherResponseDto> = (0..size).map { creationFunction(it) }


private fun generateWeatherResponseDto(): WeatherResponseDto {
    return WeatherResponseDto(
        coord = generateCoordDto(), // Assuming a method to generate random coordinates
        weather = generateWeatherInfoList(), // List of weather conditions
        main = generateTemp(), // Temperature information (TempDto)
        wind = generateWindDto(), // Assuming a method to generate wind data
        sys = generateSysDto(), // Assuming a method to generate system data (sunrise/sunset)
        name = randomString(), // City name
        visibility = randomInt(), // Visibility in meters
        clouds = generateCloudsDto(), // Cloud coverage
        dt = randomInt() // Timestamp
    )
}

private fun generateWeatherInfoList(): List<WeatherInfoDto> {
    return listOf(
        WeatherInfoDto(
            description = randomString(),
            icon = randomString(),
            id = randomInt(),
            main = randomString() // Main weather condition (e.g., "Clear", "Clouds")
        )
    )
}

private fun generateCoordDto(): CoordDto {
    return CoordDto(
        lat = randomDouble(), // Latitude
        lon = randomDouble() // Longitude
    )
}

private fun generateWindDto(): WindDto {
    return WindDto(
        speed = randomDouble(), // Wind speed
        deg = randomInt(), // Wind degree
        gust = randomDouble() // Wind gusts
    )
}

private fun generateSysDto(): SysDto {
    return SysDto(
        country = randomString(), // Country code
        sunrise = randomInt(), // Sunrise time
        sunset = randomInt() // Sunset time
    )
}

private fun generateCloudsDto(): CloudsDto {
    return CloudsDto(
        all = randomInt() // Cloudiness percentage
    )
}

private fun generateTemp(): TempDto {
    return TempDto(
        temp = randomDouble(), // Current temperature
        feels_like = randomDouble(), // Feels like temperature
        temp_min = randomDouble(), // Minimum temperature
        temp_max = randomDouble(), // Maximum temperature
        pressure = randomInt(), // Atmospheric pressure
        humidity = randomInt() // Humidity percentage
    )
}

fun generateCityInfoDto(): CityInfoDto =
    CityInfoDto(
        city = generateCity(),
        cnt = randomInt(),
        cod = randomString(),
        list = generateWeatherDtoList(),
        message = randomDouble()
    )

private fun generateCity(): CityDto =
    CityDto(
        coord = CoordDto(randomDouble(), randomDouble()),
        country = randomString(),
        id = randomInt(),
        name = randomString(),
        population = randomInt(),
        timezone = randomInt()
    )

fun randomPositiveInt(maxInt: Int = Int.MAX_VALUE - 1): Int =
    random.nextInt(maxInt + 1).takeIf { it > 0 } ?: randomPositiveInt(maxInt)

fun randomInt() = random.nextInt()
fun randomDouble() = random.nextDouble()
fun randomString(size: Int = 20): String = (0..size)
    .map { charPool[random.nextInt(0, charPool.size)] }
    .joinToString("")
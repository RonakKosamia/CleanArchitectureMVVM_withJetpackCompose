package com.rk.openweatherapp

import com.rk.openweatherapp.data.remote.dto.CityDto
import com.rk.openweatherapp.data.remote.dto.CloudsDto
import com.rk.openweatherapp.data.remote.dto.CoordDto
import com.rk.openweatherapp.data.remote.dto.DailyForecastDto
import com.rk.openweatherapp.data.remote.dto.FeelsLikeDto
import com.rk.openweatherapp.data.remote.dto.ForecastWeatherResponseDto
import com.rk.openweatherapp.data.remote.dto.SysDto
import com.rk.openweatherapp.data.remote.dto.TempDto
import com.rk.openweatherapp.data.remote.dto.TemperatureDto
import com.rk.openweatherapp.data.remote.dto.WeatherDto
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

// Generate a list of WeatherDto objects
fun generateForecastWeatherDtoList(
    size: Int = randomPositiveInt(10),
    creationFunction: (Int) -> WeatherDto = { generateWeatherDto() }
): List<WeatherDto> = (0 until size).map { creationFunction(it) }


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

fun generateCityInfoDto(): ForecastWeatherResponseDto {
    return ForecastWeatherResponseDto(
        city = generateCity(), // Generate city data
        cnt = randomPositiveInt(7), // Number of forecast days
        cod = randomString(3), // Random response code like "200"
        list = generateDailyForecastDtoList(randomPositiveInt(7)), // Generate a list of daily forecasts
        message = randomDouble() // Random message
    )
}

// Generate a list of daily forecasts
private fun generateDailyForecastDtoList(size: Int): List<DailyForecastDto> {
    return (0 until size).map { generateDailyForecastDto() }
}

// Generate a single daily forecast
private fun generateDailyForecastDto(): DailyForecastDto {
    return DailyForecastDto(
        dt = random.nextLong(), // Random Unix timestamp
        sunrise = random.nextLong(), // Random sunrise timestamp
        sunset = random.nextLong(), // Random sunset timestamp
        temp = generateTemperatureDto(), // Generate temperature data
        feels_like = generateFeelsLikeDto(), // Generate feels-like data
        pressure = randomPositiveInt(1050), // Random pressure value
        humidity = randomPositiveInt(100), // Random humidity percentage
        weather = generateForecastWeatherDtoList(1) { generateWeatherDto() }, // Weather conditions list
        speed = randomDouble(), // Random wind speed
        deg = randomPositiveInt(360), // Random wind direction
        gust = randomDouble(), // Random wind gusts
        clouds = randomPositiveInt(100), // Cloudiness percentage
        pop = randomDouble(), // Probability of precipitation
        rain = randomNullableDouble() // Optional rain volume
    )
}

// Generate temperature data
private fun generateTemperatureDto(): TemperatureDto {
    return TemperatureDto(
        day = randomDouble(), // Day temperature
        min = randomDouble(), // Minimum temperature
        max = randomDouble(), // Maximum temperature
        night = randomDouble(), // Night temperature
        eve = randomDouble(), // Evening temperature
        morn = randomDouble() // Morning temperature
    )
}

// Generate feels-like temperature data
private fun generateFeelsLikeDto(): FeelsLikeDto {
    return FeelsLikeDto(
        day = randomDouble(), // Daytime feels-like temperature
        night = randomDouble(), // Nighttime feels-like temperature
        eve = randomDouble(), // Evening feels-like temperature
        morn = randomDouble() // Morning feels-like temperature
    )
}

// Generate weather condition data
private fun generateWeatherDto(): WeatherDto {
    return WeatherDto(
        id = randomPositiveInt(999), // Random weather condition ID
        main = randomString(5), // Main weather description
        description = randomString(10), // Detailed weather description
        icon = randomString(3) // Random icon code
    )
}

// Generate nullable random double for optional fields
fun randomNullableDouble(): Double? = if (random.nextBoolean()) randomDouble() else null

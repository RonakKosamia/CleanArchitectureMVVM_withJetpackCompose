package com.rk.openweatherapp.data.remote.dto

import com.rk.openweatherapp.domain.model.Weather

// Response DTO for the weather API
data class WeatherResponseDto(
    val coord: CoordDto,              // Coordinates
    val weather: List<WeatherInfoDto>, // Weather conditions list
    val main: TempDto,                // Temperature and pressure data
    val wind: WindDto,                // Wind data
    val sys: SysDto,                  // System data (e.g., sunrise/sunset)
    val name: String,                 // City name
    val visibility: Int,              // Visibility in meters
    val clouds: CloudsDto,            // Cloud coverage
    val dt: Int                       // Time of the data
)

// Convert WeatherResponseDto to domain model Weather
fun WeatherResponseDto.toWeather(): Weather {
    return Weather(
        dt = dt,
        day = main.temp_max, // Max temperature for day
        night = main.temp_min, // Min temperature for night
        title = weather.firstOrNull()?.main ?: "", // Weather condition (e.g., Clouds, Rain)
        description = weather.firstOrNull()?.description ?: "No Desc.", // Detailed description
        icon = weather.firstOrNull()?.icon ?: "", // Weather icon
        currentTemp = main.temp,  // Current temperature
        feelsLike = main.feels_like, // Feels like temperature
        pressure = main.pressure, // Atmospheric pressure
        humidity = main.humidity, // Humidity percentage
        lat = coord.lat,
        lon = coord.lon
    )
}

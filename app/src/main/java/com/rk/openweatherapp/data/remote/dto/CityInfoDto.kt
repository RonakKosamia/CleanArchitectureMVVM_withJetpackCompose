package com.rk.openweatherapp.data.remote.dto

// Main DTO for weather forecast response
data class ForecastWeatherResponseDto(
    val city: CityDto,                  // City information
    val cod: String,                    // Response code (e.g., "200")
    val message: Double,                // Message or additional information
    val cnt: Int,                       // Number of days in the forecast
    val list: List<DailyForecastDto>    // List of daily weather forecasts
)

// City information
data class CityDto(
    val id: Int,                        // City ID
    val name: String,                   // City name
    val coord: CoordDto,                // Coordinates (lat, lon)
    val country: String,                // Country code
    val population: Int,                // Population
    val timezone: Int                   // Timezone offset from UTC
)

// Daily forecast data
data class DailyForecastDto(
    val dt: Long,                       // Time of data (Unix timestamp)
    val sunrise: Long,                  // Sunrise time (Unix timestamp)
    val sunset: Long,                   // Sunset time (Unix timestamp)
    val temp: TemperatureDto,                  // Temperature data (day, min, max, etc.)
    val feels_like: FeelsLikeDto,       // Feels-like temperatures
    val pressure: Int,                  // Atmospheric pressure
    val humidity: Int,                  // Humidity percentage
    val weather: List<WeatherDto>,      // Weather conditions list
    val speed: Double,                  // Wind speed
    val deg: Int,                       // Wind direction
    val gust: Double,                   // Wind gusts
    val clouds: Int,                    // Cloudiness percentage (as an integer)
    val pop: Double,                    // Probability of precipitation
    val rain: Double? = null            // Rain volume (optional)
)

// Temperature data for daily forecast
data class TemperatureDto(
    val day: Double,                    // Day temperature
    val min: Double,                    // Minimum temperature
    val max: Double,                    // Maximum temperature
    val night: Double,                  // Night temperature
    val eve: Double,                    // Evening temperature
    val morn: Double                    // Morning temperature
)

// Feels-like temperature data
data class FeelsLikeDto(
    val day: Double,                    // Daytime feels-like temperature
    val night: Double,                  // Nighttime feels-like temperature
    val eve: Double,                    // Evening feels-like temperature
    val morn: Double                    // Morning feels-like temperature
)

// Weather description
data class WeatherDto(
    val id: Int,                      // Weather condition ID
    val main: String,                 // Group of weather parameters (e.g., Clouds, Rain)
    val description: String,          // Weather condition within the group (e.g., overcast clouds)
    val icon: String                  // Icon code to display the weather
)



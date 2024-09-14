package com.rk.openweatherapp.domain.model

// Domain model for Weather used in your app
data class Weather(
    val dt: Int,
    val day: Double, // Max temperature during the day
    val night: Double, // Min temperature during the night
    val title: String, // Weather condition title (e.g., Clouds)
    val description: String, // Weather condition description (e.g., Few clouds)
    val icon: String, // Icon code
    val currentTemp: Double, // Current temperature
    val feelsLike: Double, // Feels like temperature
    val pressure: Int, // Atmospheric pressure
    val humidity: Int, // Humidity percentage
    val lat: Double, // Latitude
    val lon: Double  // Longitude
)

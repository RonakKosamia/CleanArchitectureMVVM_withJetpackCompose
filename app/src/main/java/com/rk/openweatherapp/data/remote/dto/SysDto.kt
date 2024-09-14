package com.rk.openweatherapp.data.remote.dto

data class SysDto(
    val country: String, // Country code
    val sunrise: Int, // Long value for sunrise
    val sunset: Int // Long value for sunset
)

package com.rk.openweatherapp.presentation.weather_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rk.openweatherapp.common.Resource
import com.rk.openweatherapp.domain.use_case.get_weather_detail.GetWeatherDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    private val getWeatherDetailUseCase: GetWeatherDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherState())
    val state = _state.asStateFlow()

    // Function to get weather data by latitude, longitude, and date
    fun getWeatherBy(lat: Double, lon: Double, date: Int) {
        getWeatherDetailUseCase(lat, lon, date).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = WeatherState(weather = result.data)
                }
                is Resource.Error -> {
                    _state.value = WeatherState(
                        error = result.message ?: "An unexpected error occurred"
                    )
                }
                is Resource.Loading -> {
                    _state.value = WeatherState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}


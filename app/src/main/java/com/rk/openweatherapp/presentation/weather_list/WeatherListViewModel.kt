package com.rk.openweatherapp.presentation.weather_list

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rk.openweatherapp.common.Resource
import com.rk.openweatherapp.domain.use_case.get_weather_list.GetWeatherListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherListViewModel @Inject constructor(
    private val getWeatherListUseCase: GetWeatherListUseCase,
    @ApplicationContext private val context: Context, // Ensure we get application context for shared prefs
    private val preferences: SharedPreferences // Injected SharedPreferences for saving the last searched city
) : ViewModel() {

    private val _state = MutableStateFlow<WeatherListState>(WeatherListState.Idle)
    val state: StateFlow<WeatherListState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _cityName = MutableStateFlow("Unknown City")
    val cityName: StateFlow<String> = _cityName.asStateFlow()

    fun isLastCityEmpty(): Boolean {
        return preferences.getString("last_city", null).isNullOrEmpty()
    }

    // Load the last searched city from SharedPreferences
    fun loadLastCity() {
        preferences.getString("last_city", null)?.let { lastCity ->
            if (lastCity.isNotEmpty()) {
                _searchQuery.value = lastCity
                _cityName.value = lastCity
                fetchWeatherByCity(lastCity)
            }
        }
    }

    // Fetch weather by city name
    fun fetchWeatherByCity(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = WeatherListState.Loading
            saveLastCity(city)
            getWeatherListUseCase.getWeatherByCity(city).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _state.value = WeatherListState.Loading
                    is Resource.Success -> {
                        resource.data?.let { weatherList ->
                            val updatedWeatherList = weatherList.map { weather ->
                                weather.copy(
                                    currentTemp = formatToTwoDecimal(weather.currentTemp),
                                    day = formatToTwoDecimal(weather.day),
                                    night = formatToTwoDecimal(weather.night)
                                )
                            }
                            _state.value = WeatherListState.Success(updatedWeatherList)
                            _cityName.value = city
                        } ?: run {
                            _state.value = WeatherListState.Error("No weather data available")
                        }
                    }
                    is Resource.Error -> {
                        _state.value = WeatherListState.Error(resource.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    // Save the last searched city in SharedPreferences
    fun saveLastCity(city: String) {
        preferences.edit().putString("last_city", city).apply()
    }

    // Clear preferences when the app is closed
    fun clearLastCity() {
        preferences.edit().remove("last_city").apply()
    }

    // Update search query
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Helper function to format temperature
    @SuppressLint("DefaultLocale")
    private fun formatToTwoDecimal(value: Double): Double {
        return String.format("%.2f", value).toDouble()
    }
}






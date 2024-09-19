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

    // Initialize MutableStateFlows properly
    private val _state = MutableStateFlow<WeatherListState>(WeatherListState.Idle)
    val state: StateFlow<WeatherListState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _cityName = MutableStateFlow("Unknown City")
    val cityName: StateFlow<String> = _cityName.asStateFlow()

//    // init block where we load the last city
//    init {
//        // Load the last searched city when ViewModel is initialized
//        loadLastCity()
//    }

    // Load the last searched city from SharedPreferences
    fun loadLastCity() {
        preferences.getString("last_city", "Atlanta,USA")?.let { lastCity ->
            // Only update if lastCity is not null or empty
            if (lastCity.isNotEmpty()) {
                _searchQuery.value = lastCity
                _cityName.value = lastCity
                // Fetch weather for the last city
                fetchWeatherByCity(lastCity)
            }
        }
    }


    // ViewModel Function to Fetch Weather by City
    fun fetchWeatherByCity(city: String) {
        Log.d("WeatherApp", "Fetching weather for $city")
        viewModelScope.launch(Dispatchers.IO) {
            // Set loading state
            _state.value = WeatherListState.Loading
            saveLastCity(city) // Save the city to preferences

            // Collect weather data
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
        _cityName.value = city
    }

    // Function to update search query
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Helper function to format temperature
    @SuppressLint("DefaultLocale")
    private fun formatToTwoDecimal(value: Double): Double {
        return String.format("%.2f", value).toDouble()
    }
}





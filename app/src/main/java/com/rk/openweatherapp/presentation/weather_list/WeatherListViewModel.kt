package com.rk.openweatherapp.presentation.weather_list

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rk.openweatherapp.common.Resource
import com.rk.openweatherapp.domain.use_case.get_weather_list.GetWeatherListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherListViewModel @Inject constructor(
    private val getWeatherListUseCase: GetWeatherListUseCase,
    private val preferences: SharedPreferences // For saving the last searched city
) : ViewModel() {

    private val _state = MutableStateFlow<WeatherListState>(WeatherListState.Loading)
    val state: StateFlow<WeatherListState> = _state.asStateFlow()
    var lastCity: String? = null

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Track the current city name
    private var _cityName = MutableStateFlow("Unknown City")
    val cityName: StateFlow<String> = _cityName.asStateFlow()

    fun fetchLastCity() {
        viewModelScope.launch {
            val lastCity = preferences.getString("last_city", null)
            if (!lastCity.isNullOrEmpty()) {
                fetchWeatherByCity(lastCity)
            } else {
                _state.value = WeatherListState.Error("No last city saved")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Update city name from input or reverse geocoding
    fun updateCityName(name: String) {
        _cityName.value = name
    }

    fun saveLastCity(city: String) {
        preferences.edit().putString("last_city", city).apply()
    }


    @SuppressLint("DefaultLocale")
    fun fetchWeatherByCity(city: String) {

        if (lastCity == city) return  // Prevent unnecessary repeated API calls for the same city
        _cityName.value = city  // Correct: Update the MutableStateFlow's value

        viewModelScope.launch {
            delay(300)  // Add a slight delay to debounce the API call if user is typing
            if (city.isEmpty()) {
                // Update the state with an error message
                _state.value = WeatherListState.Error("City name cannot be empty")
            } else {
                try {
                    getWeatherListUseCase.getWeatherByCity(city).collect { resource ->
                        when (resource) {
                            is Resource.Loading -> {
                                _state.value = WeatherListState.Loading
                            }
                            is Resource.Success -> {
                                resource.data?.let { weatherList ->
                                    // Convert temperatures from Kelvin to Celsius
                                    val updatedWeatherList = weatherList.map { weather ->
                                        weather.copy(
                                            // Convert Kelvin to Celsius and format to two decimal places
                                            currentTemp = String.format("%.2f", weather.currentTemp - 273.15).toDouble()
                                        )
                                    }

                                    _state.value = WeatherListState.Success(updatedWeatherList)
                                    lastCity = city
                                } ?: run {
                                    _state.value = WeatherListState.Error("No weather data available")
                                }
                            }
                            is Resource.Error -> {
                                _state.value = WeatherListState.Error(resource.message ?: "Unknown error")
                            }
                        }
                    }
                } catch (e: Exception) {
                    _state.value = WeatherListState.Error("Failed to fetch weather: ${e.message}")
                }
            }
        }
    }

    fun getWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            getWeatherListUseCase.getWeatherByLatLon(lat, lon).collect { result ->
                when (result) {
                    is Resource.Loading -> _state.value = WeatherListState.Loading
                    is Resource.Success -> {
                        result.data?.let {
                            _state.value = WeatherListState.Success(it)
                        } ?: run {
                            _state.value = WeatherListState.Error("No weather data available")
                        }
                    }
                    is Resource.Error -> _state.value = WeatherListState.Error(result.message ?: "Failed to fetch weather")
                }
            }
        }
    }
}


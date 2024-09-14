package com.rk.openweatherapp.presentation.weather_list

import android.content.SharedPreferences
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rk.openweatherapp.common.Resource
import com.rk.openweatherapp.domain.use_case.get_weather_list.GetWeatherListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherListViewModel @Inject constructor(
    private val getWeatherListUseCase: GetWeatherListUseCase,
    private val geocoder: Geocoder, // Inject Geocoder for conversion
    private val preferences: SharedPreferences // Inject SharedPreferences for saving the last city
) : ViewModel() {

    // Private mutable state flow that holds the current state of the UI
    private val _state = MutableStateFlow<WeatherListState>(WeatherListState.Loading)
    val state: StateFlow<WeatherListState> = _state.asStateFlow()

    // Mutable state for the search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Fetch the last searched city and get its weather
    fun fetchLastCity() {
        viewModelScope.launch(Dispatchers.IO) {
            val lastCity = preferences.getString("last_city", null)
            if (!lastCity.isNullOrEmpty()) {
                fetchWeatherByCity(lastCity)
            } else {
                _state.value = WeatherListState.Error("No last city saved")
            }
        }
    }


    // Save the last searched city in SharedPreferences
    fun saveLastCity(city: String) {
        preferences.edit().putString("last_city", city).apply()
    }

    // Update the search query in the ViewModel
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun fetchWeatherByCity(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Directly call the weather API with the city name
                val result = getWeatherListUseCase.getWeatherByCity(city)

                result.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _state.value = WeatherListState.Loading
                        is Resource.Success -> {
                            resource.data?.let {
                                _state.value = WeatherListState.Success(it)
                            } ?: run {
                                _state.value = WeatherListState.Error("No weather data available")
                            }
                        }
                        is Resource.Error -> _state.value =
                            WeatherListState.Error(resource.message ?: "Unknown error")
                    }
                }
            } catch (e: Exception) {
                _state.value = WeatherListState.Error("Error fetching data")
            }
        }
    }

    //using geocoder causing issue on emulator
    // - android.os.DeadObjectException -
    // Geocoder or the location service not being available or dying unexpectedly.
//    built-in Geocoder sometimes has issues, especially in emulators.

//    fun fetchWeatherByCity(city: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val locationList = geocoder.getFromLocationName(city, 1)
//                if (!locationList.isNullOrEmpty()) {
//                    val lat = locationList[0].latitude
//                    val lon = locationList[0].longitude
//                    getWeather(lat, lon) // Pass lat and lon to fetch weather
//                } else {
//                    _state.value = WeatherListState.Error("City not found")
//                }
//            } catch (e: Exception) {
//                _state.value = WeatherListState.Error("Failed to fetch weather")
//            }
//        }
//    }


    // Fetch weather data using lat/lon from the use case and update the state
    fun getWeather(lat: Double, lon: Double) {
//        viewModelScope.launch(Dispatchers.IO) {
//            getWeatherListUseCase(lat, lon).collect { result ->
//                when (result) {
//                    is Resource.Loading -> _state.value = WeatherListState.Loading
//                    is Resource.Success -> {
//                        result.data?.let {
//                            _state.value = WeatherListState.Success(it)
//                        } ?: run {
//                            _state.value = WeatherListState.Error("No weather data available")
//                        }
//                    }
//                    is Resource.Error -> _state.value =
//                        WeatherListState.Error(result.message ?: "Unknown error")
//                }
//            }
//        }
    }

}

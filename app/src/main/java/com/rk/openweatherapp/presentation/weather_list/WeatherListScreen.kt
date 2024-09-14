package com.rk.openweatherapp.presentation.weather_list

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import java.util.Locale


@Composable
fun WeatherListScreen(
    navController: NavController,
    viewModel: WeatherListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Use collectAsState to observe the city name properly
    val cityName by viewModel.cityName.collectAsState()

    // Request location and handle permission results
    RequestLocationPermission(
        onPermissionGranted = { fetchCurrentLocation(viewModel, context) },
        onPermissionDenied = { viewModel.fetchLastCity() }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = { Text("Enter city name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardActions = KeyboardActions(
                onDone = {
                    if (searchQuery.isNotEmpty()) {
                        viewModel.fetchWeatherByCity(searchQuery)
                        viewModel.saveLastCity(searchQuery)
                    }
                }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        // Use the collected cityName state in your Text composable
        Text(
            text = "Searching weather for: $cityName",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(8.dp)
        )

        // Handle different UI states
        when (val currentState = state) {
            is WeatherListState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is WeatherListState.Success -> {
                if (currentState.weatherList.isNotEmpty()) {
                    LazyColumn {
                        items(currentState.weatherList) { weather ->
                            WeatherItem(
                                date = weather.dt,
                                temperature = "${weather.currentTemp}°C",
                                icon = weather.icon,
                                onClick = {
                                    navController.navigate("weatherDetail/${weather.lat}/${weather.lon}/${weather.dt}")
                                }
                            )
                        }
                    }
                } else {
                    Text("No weather data available", style = MaterialTheme.typography.body1, modifier = Modifier.padding(16.dp))
                }
            }
            is WeatherListState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${currentState.message}", color = MaterialTheme.colors.error)
                }
            }
        }
    }
}


fun fetchCurrentLocation(viewModel: WeatherListViewModel, context: Context) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                viewModel.getWeather(location.latitude, location.longitude)

                // Reverse geocoding to fetch city name from latitude and longitude
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    viewModel.updateCityName(addresses[0].locality ?: "Unknown City")
                }
            } else {
                viewModel.fetchLastCity()
            }
        }
    } else {
        viewModel.fetchLastCity()
    }
}




@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    SideEffect {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}


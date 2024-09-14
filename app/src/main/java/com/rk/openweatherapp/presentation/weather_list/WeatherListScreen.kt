package com.rk.openweatherapp.presentation.weather_list

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices

@Composable
fun WeatherListScreen(
    navController: NavController,
    viewModel: WeatherListViewModel = hiltViewModel()
) {
    val context = LocalContext.current // Get context in a composable
    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Ask for location permission on screen load
    RequestLocationPermission(
        onPermissionGranted = { fetchCurrentLocation(viewModel, context) },  // Pass the context here
        onPermissionDenied = { viewModel.fetchLastCity() }  // Fetch last searched city if permission is denied
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

        when (state) {
            is WeatherListState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is WeatherListState.Success -> {
                if ((state as WeatherListState.Success).weatherList.isNotEmpty()) {
                    LazyColumn {
                        items((state as WeatherListState.Success).weatherList) { weather ->
                            WeatherItem(
                                date = weather.dt,
                                temperature = weather.currentTemp.toString() + "°C",
                                icon = weather.icon,
                                onClick = { navController.navigate("weatherDetail/${weather.lat}/${weather.lon}/{${weather.dt}}") }
                            )
                        }
                    }
                } else {
                    Text("No weather data available", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
            is WeatherListState.Error -> {
                Text("Error loading data", modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

fun fetchCurrentLocation(viewModel: WeatherListViewModel, context: Context) {
    // Check if location permission is granted before fetching location
    if (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        // Permissions are granted, proceed with fetching the location
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                // Fetch weather using the fetched lat/lon
                viewModel.getWeather(lat, lon)
            } else {
                // Fallback to last city if location is unavailable
                viewModel.fetchLastCity()
            }
        }
    } else {
        // Permissions are not granted, handle this scenario (optional: show a message)
        viewModel.fetchLastCity()  // Fallback to last searched city if permissions are not granted
    }
}


@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (isGranted) {
            onPermissionGranted()  // If permission granted, proceed to get the location
        } else {
            onPermissionDenied()   // If permission denied, fetch last searched city
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

package com.rk.openweatherapp.presentation.weather_list

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cityName by viewModel.cityName.collectAsState()
    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(false) }
    var isFirstLaunch by remember { mutableStateOf(true) } // Track if it's the first launch

    // Launch location permission request
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // On first launch, check if the app has location permission and load the last searched city or current location.
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        // If this is the first launch and no last city is found, fetch weather based on location
        if (isFirstLaunch) {
            viewModel.loadLastCity()
        }
    }

    // Fetch current location if it's the first launch and no last city exists
    LaunchedEffect(hasLocationPermission) {
        if (isFirstLaunch && hasLocationPermission && viewModel.isLastCityEmpty()) {
            fetchCurrentLocation(viewModel, context)
            isFirstLaunch = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // TextField for city search input
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
                    }
                }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        // Display current city being searched for
        Text(
            text = "Searching weather for: $cityName",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(8.dp)
        )

        // Handle UI based on current state
        when (val currentState = state) {
            is WeatherListState.Idle -> {
                Text("Please enter a city name to search.", modifier = Modifier.padding(16.dp))
            }
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
                                    navController.navigate(
                                        "weatherDetail/${weather.lat}/${weather.lon}/${weather.dt}/${weather.day}/${weather.night}/${weather.feelsLike}/${weather.pressure}/${weather.humidity}/${weather.title}/${weather.description}/${weather.icon}"
                                    )
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

// Function to fetch the user's current location
fun fetchCurrentLocation(viewModel: WeatherListViewModel, context: Context) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val cityName = addresses[0].locality ?: "Unknown City"
                    Toast.makeText(context, "Fetching weather for $cityName", Toast.LENGTH_SHORT).show()
                    viewModel.fetchWeatherByCity(cityName)
                    viewModel.saveLastCity(cityName)
                } else {
                    Toast.makeText(context, "Unable to get city name", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
    } else {
        Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
    }
}


/*

isFirstLaunch tracking:
This ensures we only fetch the current location on the first launch.
When the user navigates back to the search screen from the details screen,
it won’t call the current location again.
isLastCityEmpty():
This function checks if the last searched city exists in the preferences.
If it's empty, we fetch the current location.
clearLastCity():

This function clears the stored last searched city when the app closes.
You should call this method in an appropriate lifecycle-aware component,
like in onDestroy or in an activity's onPause method if you want to clear the data when the app is minimized or closed.

*/

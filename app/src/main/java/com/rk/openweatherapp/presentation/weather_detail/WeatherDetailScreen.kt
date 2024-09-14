package com.rk.openweatherapp.presentation.weather_detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rk.openweatherapp.domain.model.Weather
@Composable
fun WeatherDetailScreen(
    navController: NavController,
    lat: Double,
    lon: Double,
    dt: Int,
    viewModel: WeatherDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Fetch weather data when the screen is first loaded
    LaunchedEffect(Unit) {
        viewModel.getWeatherBy(lat, lon, dt)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }
            state.error.isNotEmpty() -> {
                Text(
                    text = state.error,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            state.weather != null -> {
                WeatherDetailContent(weather = state.weather!!)
            }
        }
    }
}

@Composable
fun WeatherDetailContent(weather: Weather) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = weather.title,
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = weather.description,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(text = "Temperature", style = MaterialTheme.typography.caption)
        Text(
            text = when {
                weather.day > 25 -> "Hot"
                weather.day < 10 -> "Cold"
                else -> "Normal"
            },
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(text = "Day Temperature: ${weather.day}°C", style = MaterialTheme.typography.h5)
        Text(text = "Night Temperature: ${weather.night}°C", style = MaterialTheme.typography.h5)
    }
}

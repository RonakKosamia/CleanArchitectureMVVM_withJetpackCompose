package com.rk.openweatherapp.presentation.weather_detail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    day: Float,         // Day temperature
    night: Float,       // Night temperature
    feelsLike: Float,   // Feels like temperature
    pressure: Int,      // Atmospheric pressure
    humidity: Int,      // Humidity percentage
    title: String,      // Weather condition title
    description: String, // Weather condition description
    viewModel: WeatherDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current // Get the context for Toast

    LaunchedEffect(Unit) {
        viewModel.getWeatherBy(lat,lon,dt)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()

            state.error.isNotEmpty() -> {
                WeatherDetailContentFallback(
                    day = day.toDouble(),
                    night = night.toDouble(),
                    feelsLike = feelsLike.toDouble(),
                    pressure = pressure,
                    humidity = humidity,
                    title = title,
                    description = description
                )
            }

            state.weather != null -> {
                WeatherDetailContent(state.weather!!)
            }

            else -> Text(
                text = "No weather data available",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatToTwoDecimal(kelvin: Double): Double {
    return String.format("%.2f", kelvin).toDouble()
}
//fun convertKelvinToCelsius(kelvin: Double): Double {
//    return (kelvin - 273.15).toBigDecimal().setScale(2, java.math.RoundingMode.HALF_EVEN).toDouble()
//}


@Composable
fun WeatherDetailContent(weather: Weather) {
    val dayTemp = formatToTwoDecimal(weather.day)
    val nightTemp = formatToTwoDecimal(weather.night)
    val feelsLikeTemp = formatToTwoDecimal(weather.feelsLike)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Title and Description
        Text(
            text = weather.title,
            style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = weather.description.capitalize(),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Temperature Overview
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Text(text = "Temperature Overview", style = MaterialTheme.typography.subtitle1, modifier = Modifier.padding(bottom = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TemperatureBox("Day", dayTemp)
            TemperatureBox("Night", nightTemp)
        }

        // Additional weather details
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Text(text = "Additional Information", style = MaterialTheme.typography.subtitle1, modifier = Modifier.padding(bottom = 8.dp))

        AdditionalWeatherDetails(
            feelsLike = feelsLikeTemp,
            pressure = weather.pressure,
            humidity = weather.humidity
        )
    }
}

@Composable
fun WeatherDetailContentFallback(
    day: Double,
    night: Double,
    feelsLike: Double,  // Add feelsLike
    pressure: Int,      // Add pressure
    humidity: Int,      // Add humidity
    title: String,
    description: String
) {
    val dayTemp = formatToTwoDecimal(day)
    val nightTemp = formatToTwoDecimal(night)
    val feelsLikeTemp = formatToTwoDecimal(feelsLike)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Title and Description
        Text(
            text = title,
            style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = description.capitalize(),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Temperature Overview
        Text(text = "Temperature Overview", style = MaterialTheme.typography.subtitle1, modifier = Modifier.padding(bottom = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TemperatureBox("Day", dayTemp)
            TemperatureBox("Night", nightTemp)
        }

        // Additional weather details
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Text(text = "Additional Information", style = MaterialTheme.typography.subtitle1, modifier = Modifier.padding(bottom = 8.dp))

        AdditionalWeatherDetails(
            feelsLike = feelsLikeTemp,
            pressure = pressure,
            humidity = humidity
        )
    }
}



@Composable
fun TemperatureBox(label: String, temp: Double) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, style = MaterialTheme.typography.body1)
        Text(text = "$temp°C", style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
fun AdditionalWeatherDetails(feelsLike: Double, pressure: Int, humidity: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Feels Like: $feelsLike°C", style = MaterialTheme.typography.body1)
            Text(text = "Pressure: ${pressure} hPa", style = MaterialTheme.typography.body1)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Humidity: ${humidity}%", style = MaterialTheme.typography.body1)
        }
    }
}

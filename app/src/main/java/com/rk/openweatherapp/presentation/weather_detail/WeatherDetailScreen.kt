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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.rk.openweatherapp.R

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
    iconCode: String,   // Weather icon code
    viewModel: WeatherDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getWeatherBy(lat, lon, dt)
    }

    val backgroundDrawable = when {
        description.contains("cloud", ignoreCase = true) -> R.drawable.cloudy
        description.contains("clear", ignoreCase = true) -> R.drawable.clearsky
        description.contains("rain", ignoreCase = true) -> R.drawable.rainy
        else -> R.drawable.clearsky // A default background if no match
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // Background Image
        Image(
            painter = painterResource(id = backgroundDrawable),
            contentDescription = null,
            contentScale = ContentScale.Crop, // Makes the image fill the entire box
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.7f) // Adjust opacity to ensure content is visible
        )


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
                    description = description,
                    iconCode = iconCode
                )
            }

            state.weather != null -> {
                    WeatherDetailContent(
                            temperature = state.weather!!.day,
                            feelsLike = state.weather!!.feelsLike,
                            pressure = state.weather!!.pressure,
                            humidity = state.weather!!.humidity,
                            description = state.weather!!.description,
                            iconUrl = "https://openweathermap.org/img/wn/${state.weather!!.icon}@2x.png"
                    )
//                WeatherDetailContent(state.weather!!, iconCode)
            }

            else -> Text(
                text = "No weather data available",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun WeatherDetailContent(
    temperature: Double,
    feelsLike: Double,
    pressure: Int,
    humidity: Int,
    description: String,
    iconUrl: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Weather Icon
        iconUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = description,
                modifier = Modifier.size(100.dp)
            )
        }

        // Temperature
        Text(
            text = String.format("%.2f°C", temperature),
            style = MaterialTheme.typography.h2,
            fontWeight = FontWeight.Bold
        )

        // Feels like
        Text(
            text = "Feels like ${String.format("%.2f°C", feelsLike)}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Weather description
        Text(
            text = description.capitalize(),
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Pressure and Humidity
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Pressure: ${pressure} hPa",
                style = MaterialTheme.typography.body1
            )
            Text(
                text = "Humidity: ${humidity}%",
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun WeatherDetailContentFallback(
    day: Double,
    night: Double,
    feelsLike: Double,
    pressure: Int,
    humidity: Int,
    title: String,
    description: String,
    iconCode: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Weather icon
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
        AsyncImage(
            model = iconUrl,
            contentDescription = title,
            modifier = Modifier.size(100.dp)
        )

        // Day Temperature
        Text(
            text = String.format("%.2f°C", day),
            style = MaterialTheme.typography.h2,
            fontWeight = FontWeight.Bold
        )

        // Feels like
        Text(
            text = "Feels like ${String.format("%.2f°C", feelsLike)}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Weather description
        Text(
            text = description.capitalize(),
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Pressure and Humidity
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Pressure: ${pressure} hPa",
                style = MaterialTheme.typography.body1
            )
            Text(
                text = "Humidity: ${humidity}%",
                style = MaterialTheme.typography.body1
            )
        }
    }
}


@Composable
fun MainWeatherInfo(
    temperature: Double,
    feelsLike: Double,
    description: String,
    iconCode: String // Weather icon code from OpenWeatherMap
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Weather icon from OpenWeatherMap
        Image(
            painter = rememberImagePainter("http://openweathermap.org/img/wn/$iconCode@2x.png"),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(120.dp)
        )

        // Temperature
        Text(
            text = "${temperature}°C",
            style = MaterialTheme.typography.h1.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 8.dp)
        )

        // Feels like temperature
        Text(
            text = "Feels like ${feelsLike}°C",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Weather description
        Text(
            text = description.capitalize(),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun AdditionalWeatherDetails(feelsLike: Double, pressure: Int, humidity: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Pressure: ${pressure} hPa", style = MaterialTheme.typography.body1)
            Text(text = "Humidity: ${humidity}%", style = MaterialTheme.typography.body1)
        }
    }
}

// Helper to format temperatures to two decimal points
@SuppressLint("DefaultLocale")
private fun formatToTwoDecimal(value: Double): Double {
    return String.format("%.2f", value).toDouble()
}


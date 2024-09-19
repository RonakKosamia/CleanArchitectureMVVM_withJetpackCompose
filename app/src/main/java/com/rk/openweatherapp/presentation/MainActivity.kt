package com.rk.openweatherapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rk.openweatherapp.domain.model.Weather
import com.rk.openweatherapp.presentation.weather_list.WeatherListScreen
import com.rk.openweatherapp.presentation.weather_detail.WeatherDetailScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MainScreen(navController)
        }
    }

    @Composable
    fun MainScreen(navController: NavHostController) {
        NavHost(navController = navController, startDestination = "weatherList") {
            composable("weatherList") {
                WeatherListScreen(navController)  // Pass the navController to WeatherListScreen
            }

            composable("weatherDetail/{lat}/{lon}/{dt}/{day}/{night}/{feelsLike}/{pressure}/{humidity}/{title}/{description}/{icon}",
            ) { backStackEntry ->
                val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
                val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull() ?: 0.0
                val dt = backStackEntry.arguments?.getString("dt")?.toIntOrNull() ?: 0
                val day = backStackEntry.arguments?.getString("day")?.toFloatOrNull() ?: 0f
                val night = backStackEntry.arguments?.getString("night")?.toFloatOrNull() ?: 0f
                val feelsLike = backStackEntry.arguments?.getString("feelsLike")?.toFloatOrNull() ?: 0f
                val pressure = backStackEntry.arguments?.getString("pressure")?.toIntOrNull() ?: 0
                val humidity = backStackEntry.arguments?.getString("humidity")?.toIntOrNull() ?: 0
                val title = backStackEntry.arguments?.getString("title") ?: ""
                val description = backStackEntry.arguments?.getString("description") ?: ""
                val icon = backStackEntry.arguments?.getString("icon") ?: ""

                // Pass the navController to WeatherDetailScreen
                WeatherDetailScreen(navController = navController,
                    lat = lat, lon = lon, dt = dt,
                    day, night,feelsLike, pressure, humidity, title, description, icon
                )
            }
        }
    }

}

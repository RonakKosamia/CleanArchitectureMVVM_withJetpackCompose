package com.rk.openweatherapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
            composable("weatherDetail/{lat}/{lon}/{dt}") { backStackEntry ->
                val lat = backStackEntry.arguments?.getString("lat")?.toDouble() ?: 0.0
                val lon = backStackEntry.arguments?.getString("lon")?.toDouble() ?: 0.0
                val dt = backStackEntry.arguments?.getString("dt")?.toInt() ?: 0
                // Pass the navController to WeatherDetailScreen
                WeatherDetailScreen(navController = navController, lat = lat, lon = lon, dt = dt)
            }
        }
    }

}

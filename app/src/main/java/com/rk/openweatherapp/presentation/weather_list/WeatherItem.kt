package com.rk.openweatherapp.presentation.weather_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeatherItem(
    date: Int,
    temperature: String,
    icon: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = date.toFormattedDate(), style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.width(8.dp))
        AsyncImage(
            model = "https://openweathermap.org/img/wn/${icon}@2x.png",
            contentDescription = "Weather Icon",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = temperature, style = MaterialTheme.typography.h4)
    }
}


fun Int.toFormattedDate(): String {
    val date = Date(this.toLong() * 1000) // Multiply by 1000 to convert seconds to milliseconds
    val sdf = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()) // e.g., "Monday, Jan 01"
    return sdf.format(date)
}

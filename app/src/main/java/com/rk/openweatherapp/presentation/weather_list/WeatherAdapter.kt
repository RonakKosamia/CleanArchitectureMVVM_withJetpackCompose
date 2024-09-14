package com.rk.openweatherapp.presentation.weather_list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rk.openweatherapp.R
import com.rk.openweatherapp.domain.model.Weather
import java.text.SimpleDateFormat
import java.util.Date

class WeatherAdapter(
    private val onWeatherClick: (weather: Weather) -> Unit
) : ListAdapter<Weather, WeatherViewHolder>(WeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return WeatherViewHolder(layoutInflater, parent, onWeatherClick)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = getItem(position) // `getItem()` is from `ListAdapter`
        holder.bind(weather)
    }
}

class WeatherViewHolder(
    layoutInflater: LayoutInflater,
    parentView: ViewGroup,
    private val onWeatherClick: (weather: Weather) -> Unit
) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_weather, parentView, false)) {

    private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    private val weatherTextView: TextView = itemView.findViewById(R.id.weatherTextView)
    private val weatherIcon: ImageView = itemView.findViewById(R.id.imageView)

    @SuppressLint("SetTextI18n")
    fun bind(weather: Weather) {
        itemView.setOnClickListener { onWeatherClick(weather) }
        dateTextView.text = weather.dt.toDate()
        weatherTextView.text = "${weather.day}°C"
        // Load the weather icon using Coil
        weatherIcon.load("https://openweathermap.org/img/wn/${weather.icon}@2x.png")
//        weatherIcon.load("https://openweathermap.org/img/w/${weather.icon}.png")
    }
}

class WeatherDiffCallback : DiffUtil.ItemCallback<Weather>() {
    override fun areItemsTheSame(oldItem: Weather, newItem: Weather): Boolean {
        return oldItem.dt == newItem.dt // Compare items by their unique ID (dt here)
    }

    override fun areContentsTheSame(oldItem: Weather, newItem: Weather): Boolean {
        return oldItem == newItem // Compare entire content to check for changes
    }
}

@SuppressLint("SimpleDateFormat")
private fun Int.toDate(): String? {
    return try {
        val sdf = SimpleDateFormat("EEEE, MMM dd")
        val netDate = Date(this.toLong() * 1000)
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}
